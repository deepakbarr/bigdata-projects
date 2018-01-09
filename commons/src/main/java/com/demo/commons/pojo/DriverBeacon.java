package com.demo.commons.pojo;

import com.github.davidmoten.geo.LatLong;

/**
 * Created by dbarr on 12/24/17.
 */
public class DriverBeacon {
    private String driverId;
    private LatLong position;
    private Long timestamp;

    public DriverBeacon(String driverId, LatLong position, Long timestamp) {
        this.driverId = driverId;
        this.position = position;
        this.timestamp = timestamp;
    }

    public String getDriverId() {
        return driverId;
    }

    public LatLong getPosition() {
        return position;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "DriverBeacon{" +
                "driverId='" + driverId + '\'' +
                ", position=" + position +
                ", timestamp=" + timestamp +
                '}';
    }
}