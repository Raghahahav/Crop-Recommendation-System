package com.smartcrop.recommendation.service;

import com.smartcrop.recommendation.model.RecommendationRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SparkCropRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(SparkCropRecommendationService.class);

    private SparkSession spark;
    private VectorAssembler assembler;
    private RandomForestClassificationModel model;
    private String[] labels;
    private final List<Sample> fallbackSamples = new ArrayList<>();
    private volatile boolean sparkAvailable = false;

    @PostConstruct
    public void initializeModel() {
        String csvPath = resolveDatasetPath();

        try {
            spark = SparkSession.builder()
                    .appName("Smart Crop Recommendation")
                    .master("local[*]")
                    .config("spark.ui.enabled", "false")
                    .config("spark.sql.warehouse.dir", "file:/tmp/spark-warehouse")
                    .getOrCreate();

            String[] featureColumns = {"N", "P", "K", "temperature", "humidity", "ph", "rainfall"};

            assembler = new VectorAssembler()
                    .setInputCols(featureColumns)
                    .setOutputCol("features");

            log.info("Training Spark ML model using dataset: {}", csvPath);

            Dataset<Row> data = spark.read()
                    .option("header", true)
                    .option("inferSchema", true)
                    .csv(csvPath);

            Dataset<Row> assembledData = assembler.transform(data);

            StringIndexerModel indexerModel = new StringIndexer()
                    .setInputCol("label")
                    .setOutputCol("labelIndex")
                    .fit(assembledData);

            Dataset<Row> indexedData = indexerModel.transform(assembledData);

            RandomForestClassifier rf = new RandomForestClassifier()
                    .setFeaturesCol("features")
                    .setLabelCol("labelIndex")
                    .setNumTrees(80)
                    .setMaxDepth(12)
                    .setSeed(42L);

            model = rf.fit(indexedData);
            labels = indexerModel.labels();
            sparkAvailable = true;

            log.info("Model ready. Learned crop labels: {}", Arrays.toString(labels));
        } catch (Exception e) {
            sparkAvailable = false;
            log.warn("Spark model initialization failed. Falling back to KNN predictor. Cause: {}", e.getMessage());

            if (spark != null) {
                spark.stop();
                spark = null;
            }

            loadFallbackSamples(csvPath);
        }
    }

    public synchronized String recommendCrop(RecommendationRequest req) {
        if (sparkAvailable && spark != null && assembler != null && model != null && labels != null) {
            return recommendWithSpark(req);
        }

        if (fallbackSamples.isEmpty()) {
            throw new IllegalStateException("No prediction model available");
        }

        return recommendWithFallback(req);
    }

    private String recommendWithSpark(RecommendationRequest req) {

        StructType schema = new StructType(new StructField[]{
                new StructField("N", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("P", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("K", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("temperature", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("humidity", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("ph", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("rainfall", DataTypes.DoubleType, false, Metadata.empty())
        });

        Row inputRow = RowFactory.create(
                req.getN(),
                req.getP(),
                req.getK(),
                req.getTemperature(),
                req.getHumidity(),
                req.getPh(),
                req.getRainfall()
        );

        Dataset<Row> inputDf = spark.createDataFrame(Collections.singletonList(inputRow), schema);
        Dataset<Row> featuresDf = assembler.transform(inputDf);
        Dataset<Row> predictionDf = model.transform(featuresDf);

        double predictionIndex = predictionDf.select("prediction").first().getDouble(0);
        int index = (int) predictionIndex;

        if (index < 0 || index >= labels.length) {
            throw new IllegalStateException("Predicted crop index out of range: " + index);
        }

        return toTitleCase(labels[index]);
    }

    private String recommendWithFallback(RecommendationRequest req) {
        final int k = 7;

        List<SampleDistance> nearest = fallbackSamples.stream()
                .map(sample -> new SampleDistance(sample, sample.distanceTo(req)))
                .sorted(Comparator.comparingDouble(SampleDistance::distance))
                .limit(k)
                .toList();

        if (nearest.isEmpty()) {
            throw new IllegalStateException("Unable to compute fallback prediction");
        }

        Map<String, Double> voteScore = new HashMap<>();
        for (SampleDistance sd : nearest) {
            double weight = 1.0 / (sd.distance() + 1e-9);
            voteScore.merge(sd.sample().label(), weight, Double::sum);
        }

        String bestLabel = voteScore.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("Fallback voting failed"));

        return toTitleCase(bestLabel);
    }

    private void loadFallbackSamples(String csvPath) {
        fallbackSamples.clear();
        Path path = Paths.get(csvPath);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String header = reader.readLine();
            if (header == null) {
                throw new IllegalStateException("Dataset is empty: " + csvPath);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 8) {
                    continue;
                }

                fallbackSamples.add(new Sample(
                        Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]),
                        Double.parseDouble(parts[5]),
                        Double.parseDouble(parts[6]),
                        parts[7].trim()
                ));
            }

            log.info("Fallback KNN predictor initialized with {} samples", fallbackSamples.size());
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Failed to load fallback predictor dataset", e);
        }
    }

    private String resolveDatasetPath() {
        try {
            URL resource = getClass().getClassLoader().getResource("crop_data.csv");
            if (resource != null) {
                return Paths.get(resource.toURI()).toString();
            }

            Path localBackendPath = Paths.get("crop_data.csv").toAbsolutePath();
            if (Files.exists(localBackendPath)) {
                return localBackendPath.toString();
            }

            Path parentWorkspacePath = Paths.get("..", "crop_data.csv").toAbsolutePath().normalize();
            if (Files.exists(parentWorkspacePath)) {
                return parentWorkspacePath.toString();
            }
        } catch (Exception ignored) {
            // Fallback exception below
        }

        throw new IllegalStateException("Could not locate crop_data.csv. Put it in backend/src/main/resources or project root.");
    }

    private String toTitleCase(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }
        return raw.substring(0, 1).toUpperCase() + raw.substring(1).toLowerCase();
    }

    @PreDestroy
    public void shutdown() {
        if (spark != null) {
            spark.stop();
        }
    }

    private record Sample(
            double n,
            double p,
            double k,
            double temperature,
            double humidity,
            double ph,
            double rainfall,
            String label
    ) {
        double distanceTo(RecommendationRequest req) {
            double dn = n - req.getN();
            double dp = p - req.getP();
            double dk = k - req.getK();
            double dTemp = temperature - req.getTemperature();
            double dHum = humidity - req.getHumidity();
            double dPh = ph - req.getPh();
            double dRain = rainfall - req.getRainfall();

            return Math.sqrt(
                    dn * dn +
                    dp * dp +
                    dk * dk +
                    dTemp * dTemp +
                    dHum * dHum +
                    dPh * dPh +
                    dRain * dRain
            );
        }
    }

    private record SampleDistance(Sample sample, double distance) {
    }
}
