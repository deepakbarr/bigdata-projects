package com.demo.commons;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;

/**
 * Created by dbarr on 12/24/17.
 */
public class GeoUtil {
    private static final int GEO_PRECISION = 6;

    public static String toGeohash(double lat, double lon) {
        return GeoHash.encodeHash(lat, lon, GEO_PRECISION);
    }

    public static String toGeohash(LatLong latlong) {
        return GeoHash.encodeHash(latlong, GEO_PRECISION);
    }

    public static LatLong toLatLong(String geohash) {
        return GeoHash.decodeHash(geohash);
    }
}
