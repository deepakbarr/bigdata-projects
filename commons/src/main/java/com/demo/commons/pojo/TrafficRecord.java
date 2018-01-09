package com.demo.commons.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dbarr on 12/22/17.
 */

public class TrafficRecord {

    @JsonProperty
    private String geohash;
    @JsonProperty
    private double timestamp;
    @JsonProperty("avgspeed")
    private double avgSpeed;
    @JsonProperty("normalized")
    private double normalizedSpeed;
    private String weather;

    public TrafficRecord() {
    }

    public TrafficRecord(String geohash, double timestamp, double avgSpeed, double normalizedSpeed, String weather) {
        this.geohash = geohash;
        this.timestamp = timestamp;
        this.avgSpeed = avgSpeed;
        this.normalizedSpeed = normalizedSpeed;
        this.weather = weather;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getNormalizedSpeed() {
        return normalizedSpeed;
    }

    public void setNormalizedSpeed(double normalizedSpeed) {
        this.normalizedSpeed = normalizedSpeed;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}


