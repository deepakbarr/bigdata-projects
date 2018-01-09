package com.demo.commons.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dbarr on 12/23/17.
 */
public class OutputRecord {

    @JsonProperty
    private long supply;
    @JsonProperty
    private long demand;
    @JsonProperty
    private double ratio;
    @JsonProperty
    private String weather;

    public OutputRecord() {
    }

    public OutputRecord(long supply, long demand, double ratio, String weather) {
        this.supply = supply;
        this.demand = demand;
        this.ratio = ratio;
        this.weather = weather;
    }

    public long getSupply() {
        return supply;
    }

    public long getDemand() {
        return demand;
    }

    public double getRatio() {
        return ratio;
    }

    public String getWeather() {
        return weather;
    }

    @Override
    public String toString() {
        return "OutputRecord{" +
                "supply=" + supply +
                ", demand=" + demand +
                ", ratio=" + ratio +
                ", weather='" + weather + '\'' +
                '}';
    }
}
