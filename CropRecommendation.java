import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.feature.StringIndexer;

import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.classification.RandomForestClassificationModel;

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;

public class CropRecommendation {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Crop Recommendation System")
                .master("local[*]")
                .getOrCreate();

        // Load dataset
        Dataset<Row> data = spark.read()
                .option("header", true)
                .option("inferSchema", true)
                .csv("crop_data.csv");

        data.show();

        // Feature columns
        String[] featureColumns = {
                "N","P","K",
                "temperature",
                "humidity",
                "ph",
                "rainfall"
        };

        // Convert features into vector
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(featureColumns)
                .setOutputCol("features");

        Dataset<Row> assembledData = assembler.transform(data);

        // Convert crop label to numeric
        StringIndexer indexer = new StringIndexer()
                .setInputCol("label")
                .setOutputCol("labelIndex");

        Dataset<Row> indexedData = indexer.fit(assembledData).transform(assembledData);

        // Train Test Split
        Dataset<Row>[] splits = indexedData.randomSplit(new double[]{0.8,0.2});
        Dataset<Row> train = splits[0];
        Dataset<Row> test = splits[1];

        // Random Forest Model
        RandomForestClassifier rf = new RandomForestClassifier()
                .setFeaturesCol("features")
                .setLabelCol("labelIndex")
                .setNumTrees(50);

        RandomForestClassificationModel model = rf.fit(train);

        // Predictions
        Dataset<Row> predictions = model.transform(test);

        predictions.select("label","prediction").show();

        // Evaluate accuracy
        MulticlassClassificationEvaluator evaluator =
                new MulticlassClassificationEvaluator()
                        .setLabelCol("labelIndex")
                        .setPredictionCol("prediction")
                        .setMetricName("accuracy");

        double accuracy = evaluator.evaluate(predictions);

        System.out.println("Model Accuracy = " + accuracy);

        spark.stop();
    }
}
