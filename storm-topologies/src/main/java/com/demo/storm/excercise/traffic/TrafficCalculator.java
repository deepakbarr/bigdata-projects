package com.demo.storm.excercise.traffic;

import com.demo.commons.pojo.DriverBeacon;
import com.github.davidmoten.geo.LatLong;
import org.apache.commons.lang.time.DateUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dbarr on 12/23/17.
 */
public class TrafficCalculator extends BaseWindowedBolt {

    private OutputCollector collector;

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.MINUTE);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, List<DriverBeacon>> dataMap = new HashMap();
        JSONParser parser = new JSONParser();

        for (Tuple t : tupleWindow.get()) {
            try {
                JSONObject jsonObject = (JSONObject) parser.parse(t.getString(0));
                String driverId = (String) jsonObject.get("driver_id");
                if (!dataMap.containsKey(driverId)) {
                    dataMap.put(driverId, new ArrayList());
                }
                List<DriverBeacon> beacons = dataMap.get(driverId);
                double lat = (Double) jsonObject.get("curr_latitude");
                double lon = (Double) jsonObject.get("curr_longitude");
                Long timestamp = formatter.parse((String) jsonObject.get("timestamp")).getTime();
                LatLong latLong = new LatLong(lat, lon);
                beacons.add(new DriverBeacon(driverId, latLong, timestamp));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        Map<String, List<Double>> superSet = new HashMap<>();
        for (List<DriverBeacon> beacons : dataMap.values()) {
            Map<String, List<Double>> speeds = TrafficUtility.getSpeedValues(beacons);
            TrafficUtility.merge(superSet, speeds);
        }

        for (String geoHash : superSet.keySet()) {
            double avgSpeed = TrafficUtility.computeMean(superSet.get(geoHash));
            double normalizedSpeed = TrafficUtility.normalize(avgSpeed);
            collector.emit(new Values(geoHash, cal.getTime().getTime(), avgSpeed, normalizedSpeed));
        }
        System.out.println("Traffic Calculator Bolt => Rows published => " + superSet.size());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("geohash", "timestamp", "avgspeed", "normalized"));
    }
}
