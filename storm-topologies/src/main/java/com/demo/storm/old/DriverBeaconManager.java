package com.demo.storm.old;

import com.github.davidmoten.geo.LatLong;

import java.util.*;

/**
 * Created by dbarr on 12/23/17.
 */
public class DriverBeaconManager {
    Map<String, TreeMap<Long, DriverBeacon>> beaconMap;

    public DriverBeaconManager() {
        beaconMap = new HashMap<>();
    }

    public void addBeacon(DriverBeacon beacon) {

        String driverId = beacon.getDriverId();

        if (!beaconMap.containsKey(driverId)) {
            beaconMap.put(driverId, new TreeMap());
        }
        TreeMap<Long, DriverBeacon> observations = beaconMap.get(driverId);
        observations.put(beacon.getTimestamp(), beacon);
    }

    public double computeAverageSpeed() {
        List<Double> averageSpeeds = new ArrayList();
        long timeTaken = 0;
        double distance = 0;
        for (TreeMap observations : beaconMap.values()) {
            Iterator<Map.Entry<Long, DriverBeacon>> iterator = observations.entrySet().iterator();
            Map.Entry<Long, DriverBeacon> last = iterator.hasNext() ? iterator.next() : null;
            while (iterator.hasNext()) {
                Map.Entry<Long, DriverBeacon> curr = iterator.next();
                timeTaken += timeDifference(curr.getValue(), last.getValue());
                distance += distanceTravelled(curr.getValue(), last.getValue());
            }

            if (distance > 0 && timeTaken > 0)
                averageSpeeds.add(distance / timeTaken);
        }
        if (averageSpeeds.isEmpty())
            return -1;
        return computeMean(averageSpeeds);

    }

    private double computeMean(List<Double> averageSpeeds) {
        double sum = 0;
        for (Double d : averageSpeeds) {
            sum += d;
        }
        return sum / averageSpeeds.size();
    }

    private double distanceTravelled(DriverBeacon curr, DriverBeacon last) {
        return distance(curr.getPosition().getLat(), curr.getPosition().getLon(), last.getPosition().getLat(), last.getPosition().getLon(), 'K');
    }

    private double timeDifference(DriverBeacon curr, DriverBeacon last) {
        return curr.getTimestamp() - last.getTimestamp();
    }

    public static final double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    private static final double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static final double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}

class DriverBeacon {
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
}
