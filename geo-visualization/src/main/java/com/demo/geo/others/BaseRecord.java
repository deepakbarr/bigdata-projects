package com.demo.geo.others;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dbarr on 12/22/17.
 */

public class BaseRecord {

    @JsonProperty("customer_id")
    private String id;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("curr_latitude")
    private Double curr_latitude;
    @JsonProperty("curr_longitude")
    private Double curr_longitude;


    public BaseRecord() {
    }

    public BaseRecord(String id, String timestamp, Double curr_latitude, Double curr_longitude) {
        this.id = id;
        this.timestamp = timestamp;
        this.curr_latitude = curr_latitude;
        this.curr_longitude = curr_longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getCurr_latitude() {
        return curr_latitude;
    }

    public void setCurr_latitude(Double curr_latitude) {
        this.curr_latitude = curr_latitude;
    }

    public Double getCurr_longitude() {
        return curr_longitude;
    }

    public void setCurr_longitude(Double curr_longitude) {
        this.curr_longitude = curr_longitude;
    }


    @Override
    public String toString() {
        return "BaseRecord{" +
                "id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", curr_latitude=" + curr_latitude +
                ", curr_longitude=" + curr_longitude +
                '}';
    }
}


