package com.geo.service;

import com.demo.commons.GeoUtil;
import com.github.davidmoten.geo.LatLong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dbarr on 1/7/18.
 */
public class TestData {
    private List<String> geohash;
    private Iterator<String> iterator = null;

    public TestData() {
        geohash = new ArrayList();
        geohash.add("dr5ry3");
        geohash.add("dr5ryn");
        geohash.add("dr5rsu");
        geohash.add("dr5rkn");
        geohash.add("dr5rs1");
        geohash.add("dr5rkj");
        geohash.add("dr5rs3");
        geohash.add("dr5ry9");
        geohash.add("dr5rkw");
        geohash.add("dr5rks");
        geohash.add("dr5rvg");
        geohash.add("dr5rth");
        geohash.add("dr5rvc");
        geohash.add("dr5rky");
        geohash.add("dr5rz4");
        geohash.add("dr5rtn");
        geohash.add("dr5rzu");
        geohash.add("dr5rv1");
        geohash.add("dr5rxt");
        geohash.add("dr72jh");
        geohash.add("dr5rxn");
        geohash.add("dr5rtj");
        geohash.add("dr5x83");
        geohash.add("dr5rt5");
        geohash.add("dr72jn");
        geohash.add("dr5rt7");
        geohash.add("dr72jj");
        geohash.add("dr72jk");
        geohash.add("dr5rvu");
        geohash.add("dr5rv6");
        geohash.add("dr72j5");
        geohash.add("dr5ryc");
        geohash.add("dr72j2");
        geohash.add("dr72j3");
        geohash.add("dr5ryj");
        geohash.add("dr5rsg");
        geohash.add("dr5ryf");
        geohash.add("dr5ryg");
        geohash.add("dr5ryh");
        init();
    }

    public void init() {
        iterator = geohash.iterator();
    }

    public LocationDTO getNext() {
        if (null != iterator && iterator.hasNext()) {
            String gh = iterator.next();
            LatLong ll = GeoUtil.toLatLong(gh);
            return new LocationDTO(ll.getLat(), ll.getLon(), "Geohash is " + gh);
        }
        init();
        return getNext();
    }
}
