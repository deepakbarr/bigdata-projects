package com.geo.service;

import com.demo.commons.GeoUtil;
import com.demo.commons.pojo.OutputRecord;
import com.demo.commons.pojo.TrafficRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.davidmoten.geo.LatLong;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dbarr on 1/7/18.
 */
public class DataUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String toHTML(Map<String, String> pairs) {
        StringBuilder sb = new StringBuilder();
        for (String key : pairs.keySet()) {
            sb.append("<b>").append(key).append(":").append("</b> &emsp;").append(pairs.get(key)).append("<br>");
        }
        return sb.toString();
    }

    public static LocationDTO toTrafficGeoPoint(Map<String, String> records) throws IOException {
        for (Map.Entry<String, String> entry : records.entrySet()) {
            TrafficRecord tr = mapper.readValue(entry.getValue(), TrafficRecord.class);
            LatLong ll = GeoUtil.toLatLong(tr.getGeohash());

            Map<String, String> pairs = ImmutableMap.of("Avg. Speed", String.format("%.2f", tr.getAvgSpeed()),
                    "Normalized", String.format("%.2f", tr.getNormalizedSpeed()),
                    "Weather", tr.getWeather());
            return new LocationDTO(ll.getLat(), ll.getLon(), toHTML(pairs));
        }
        return null;
    }

    public static LocationDTO toSupplyDemandGeoPoint(Map<String, String> records) throws IOException {
        for (Map.Entry<String, String> entry : records.entrySet()) {
            OutputRecord or = mapper.readValue(entry.getValue(), OutputRecord.class);
            LatLong ll = GeoUtil.toLatLong(entry.getKey());

            Map<String, String> pairs = ImmutableMap.of("S/D Ratio", String.format("%.2f", or.getRatio()),
                    "Weather", or.getWeather());
            return new LocationDTO(ll.getLat(), ll.getLon(), toHTML(pairs));
        }
        return null;
    }
}