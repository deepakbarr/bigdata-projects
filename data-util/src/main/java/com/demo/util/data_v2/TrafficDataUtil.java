package com.demo.util.data_v2;

import com.demo.commons.GeoUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dbarr on 12/22/17.
 */
public class TrafficDataUtil extends DataUtil {

    private static final String SEPARATOR = ",";
    private static final String ID_PREFIX = "DR";
    private static final Long INITIAL_ID = 44000l;
    private static final String OUTPUT_FILE = "/Users/dbarr/Garbage/data/supply_data_traffic_v3.csv";
    private static final String INPUT_FILE = "/Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/data-util/src/main/resources/test_set3";
    public static final String ID_KEY = "driver_id";
    private static Map<String, List<String>> geoMap = new HashMap();
    private static final int totalDrivers = 23;
    static final double rangeMin = 0.3;
    static final double rangeMax = 2.0;

    public static void main(String[] args) throws JsonProcessingException {
        List<String> rows = new Reader().read(INPUT_FILE);
        System.out.println("rows = " + rows);
        List<String> transformed = transform(rows);
        System.out.println("transformed = " + transformed.size());
        System.out.println("geoMap = " + geoMap.size());

        int count = 0;
        for (String key : geoMap.keySet()) {
            if (geoMap.get(key).size() > 5) {
                if (count++ < 100)
                    System.out.println(String.format("%s^%s", key, geoMap.get(key)));
            }
        }
        new Writer().write(OUTPUT_FILE, transformed);
    }

    public static List<String> transform(List<String> geohashList) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> transformed = new ArrayList();
        TrafficBaseRecord record;
        int i = 1;
        long initialTime = System.currentTimeMillis();
        for (String geohash : geohashList) {
            List<LatLong> latLongs = extractLatLong(geohash);

            int drivers = (int) (getRandomDouble() * latLongs.size());
            for (int d = 0; d < drivers; d++) {
                String driverId = ID_PREFIX + (INITIAL_ID + (++i));
                long current = initialTime + (long) (20000 * Math.random());
                for (LatLong ll : latLongs) {
                    if (Math.random() > 0.75)
                        continue;
                    record = new TrafficBaseRecord(driverId, formatter.format(new Date(current += 5000)), ll.getLat(), ll.getLon());
                    try {
                        addToGeoMap(record.getCurr_latitude(), record.getCurr_longitude());
                        String json = mapper.writeValueAsString(record);
                        transformed.add(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return transformed;
    }

    private static List<LatLong> extractLatLong(String geohash) {
        System.out.println("geohash = " + geohash);
        String ll = geohash.split("\\^")[1];
        String[] lls = ll.substring(1, ll.length() - 1).split(",");

        List<LatLong> list = new ArrayList();
        for (String ll2 : lls) {
            String[] tokens = ll2.trim().split("\\|");
            list.add(new LatLong(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1])));
        }
        return list;
    }

    private static void addToGeoMap(Double lat, Double lon) {
        String geoHash = GeoUtil.toGeohash(lat, lon);
        if (!geoMap.containsKey(geoHash)) {
            geoMap.put(geoHash, new ArrayList());
        }
        geoMap.get(geoHash).add(String.format("%s|%s", lat, lon));
    }

    private static double getRandomDouble() {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }
}

class TrafficBaseRecord {

    @JsonProperty(TrafficDataUtil.ID_KEY)
    private String id;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("curr_latitude")
    private Double curr_latitude;
    @JsonProperty("curr_longitude")
    private Double curr_longitude;

    public TrafficBaseRecord(String id, String timestamp, Double curr_latitude, Double curr_longitude) {
        this.id = id;
        this.timestamp = timestamp;
        this.curr_latitude = curr_latitude;
        this.curr_longitude = curr_longitude;
    }

    public String getId() {
        return id;
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
}

