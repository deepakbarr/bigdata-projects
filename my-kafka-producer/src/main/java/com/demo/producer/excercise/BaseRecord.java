package com.demo.producer.excercise;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseRecord {

    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("curr_latitude")
    private Double curr_latitude;
    @JsonProperty("curr_longitude")
    private Double curr_longitude;

    public BaseRecord() {
    }

    public BaseRecord(String timestamp, Double curr_latitude, Double curr_longitude) {
        this.timestamp = timestamp;
        this.curr_latitude = curr_latitude;
        this.curr_longitude = curr_longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getCurr_latitude() {
        return curr_latitude;
    }

    public Double getCurr_longitude() {
        return curr_longitude;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}


class SupplyBaseRecord extends BaseRecord {

    @JsonProperty("driver_id")
    private String id;

    public SupplyBaseRecord() {
    }

    public SupplyBaseRecord(String id, String timestamp, Double curr_latitude, Double curr_longitude) {
        super(timestamp, curr_latitude, curr_longitude);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

class DemandBaseRecord extends BaseRecord {

    @JsonProperty("customer_id")
    private String id;

    public DemandBaseRecord() {
    }

    public DemandBaseRecord(String id, String timestamp, Double curr_latitude, Double curr_longitude) {
        super(timestamp, curr_latitude, curr_longitude);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}