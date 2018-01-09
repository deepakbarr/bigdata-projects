package com.demo.storm.excercise.traffic;

import com.demo.commons.GeoUtil;
import com.demo.commons.pojo.DriverBeacon;
import com.github.davidmoten.geo.LatLong;

import java.util.*;

/**
 * Created by dbarr on 12/24/17.
 */
public class TrafficUtility {

    private static final double MAX_SPEED = 16;

    public static Map<String, List<Double>> getSpeedValues(List<DriverBeacon> beacons) {

        sortByTimestamp(beacons);

        Map<String, List<Double>> geohashSpeedMap = new HashMap();
        Iterator<DriverBeacon> iterator = beacons.iterator();
        DriverBeacon lastBeacon = null, currentBeacon;

        if (iterator.hasNext())
            lastBeacon = iterator.next();

        while (iterator.hasNext()) {
            currentBeacon = iterator.next();
            if (sameGeohash(currentBeacon, lastBeacon)) {
                double dist = distance(currentBeacon.getPosition(), lastBeacon.getPosition());
                double time = timeDifference(currentBeacon, lastBeacon);
                if (time == 0)
                    continue;
                double avgSpeed = toMetersPerSecond(dist / time);
                String geohash = GeoUtil.toGeohash(currentBeacon.getPosition());

                if (!geohashSpeedMap.containsKey(geohash)) {
                    geohashSpeedMap.put(geohash, new ArrayList());
                }
                geohashSpeedMap.get(geohash).add(avgSpeed);
                lastBeacon = currentBeacon;
            }
        }
        return geohashSpeedMap;
    }

    public static void sortByTimestamp(List<DriverBeacon> beacons) {
        Comparator<DriverBeacon> COMPARATOR = (o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp());
        Collections.sort(beacons, COMPARATOR);
    }

    private static boolean sameGeohash(DriverBeacon beacon1, DriverBeacon beacon2) {
        if (beacon1 == null || beacon2 == null)
            return false;
        return GeoUtil.toGeohash(beacon1.getPosition()).equals(GeoUtil.toGeohash(beacon2.getPosition()));
    }

    public static Map<String, List<Double>> merge(Map<String, List<Double>> map1, Map<String, List<Double>> map2) {
        for (String key : map1.keySet()) {
            if (map2.containsKey(key)) {
                List<Double> value1 = map1.get(key);
                List<Double> value2 = map2.get(key);
                value1.addAll(value2);
                map2.remove(key);
            }
        }
        map1.putAll(map2);
        return map1;
    }

    public static double computeMean(List<Double> values) {
        double sum = 0;
        for (Double d : values) {
            sum += d;
        }
        return sum / values.size();
    }

    public static double normalize(double value) {
        double normalized = value / MAX_SPEED;
        return normalized > 1 ? 1 : normalized;
    }


    private static double toMetersPerSecond(double value) {
        return value * Math.pow(10, 6);
    }

    private static double timeDifference(DriverBeacon curr, DriverBeacon last) {
        return curr.getTimestamp() - last.getTimestamp();
    }

    public static final double distance(LatLong latLong1, LatLong latLong2) {
        return distance(latLong1.getLat(), latLong1.getLon(), latLong2.getLat(), latLong2.getLon());
    }

    public static final double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; //Convert to KM
        return (dist);
    }

    private static final double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static final double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
