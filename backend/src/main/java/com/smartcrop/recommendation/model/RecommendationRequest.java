package com.smartcrop.recommendation.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class RecommendationRequest {

    @NotNull
    @DecimalMin(value = "0")
    @JsonProperty("N")
    @JsonAlias({"n"})
    private Double N;

    @NotNull
    @DecimalMin(value = "0")
    @JsonProperty("P")
    @JsonAlias({"p"})
    private Double P;

    @NotNull
    @DecimalMin(value = "0")
    @JsonProperty("K")
    @JsonAlias({"k"})
    private Double K;

    @NotNull
    @DecimalMin(value = "-50")
    @DecimalMax(value = "80")
    private Double temperature;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private Double humidity;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "14")
    private Double ph;

    @NotNull
    @DecimalMin(value = "0")
    private Double rainfall;

    public Double getN() {
        return N;
    }

    public void setN(Double n) {
        N = n;
    }

    public Double getP() {
        return P;
    }

    public void setP(Double p) {
        P = p;
    }

    public Double getK() {
        return K;
    }

    public void setK(Double k) {
        K = k;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Double getRainfall() {
        return rainfall;
    }

    public void setRainfall(Double rainfall) {
        this.rainfall = rainfall;
    }
}
