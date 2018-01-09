package com.demo.storm.old;

import com.demo.commons.GeoUtil;
import com.demo.commons.WeatherUtil;
import com.demo.commons.pojo.TrafficRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private ObjectMapper mapper = new ObjectMapper();

    private final String TRAFFIC_KEY_PREFIX = "TRAFFIC.";

    private OutputCollector collector;

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.MINUTE);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, DriverBeaconManager> dataMap = new HashMap();
        DriverBeaconManager beaconManager;
        JSONParser parser = new JSONParser();

        for (Tuple t : tupleWindow.get()) {
            try {
                JSONObject jsonObject = (JSONObject) parser.parse(t.getString(0));
                double lat = (Double) jsonObject.get("curr_latitude");
                double lon = (Double) jsonObject.get("curr_longitude");
                String geoHash = GeoUtil.toGeohash(lat, lon);
                if (!dataMap.containsKey(geoHash)) {
                    dataMap.put(geoHash, new DriverBeaconManager());
                }
                beaconManager = dataMap.get(geoHash);
                String driverId = (String) jsonObject.get("driver_id");
                Long timestamp = formatter.parse((String) jsonObject.get("timestamp")).getTime();
                LatLong latLong = new LatLong(lat, lon);
                beaconManager.addBeacon(new DriverBeacon(driverId, latLong, timestamp));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        for (String geoHash : dataMap.keySet()) {
            //TO-DO Normalize the average speed into 0 and 1

            double avgSpeed = dataMap.get(geoHash).computeAverageSpeed();
            if (avgSpeed >= 0) {
                avgSpeed = avgSpeed * Math.pow(10, 6);

                String weatherInfo = WeatherUtil.getWeatherInfo(geoHash);

                TrafficRecord record = new TrafficRecord(geoHash, cal.getTime().getTime(), avgSpeed, avgSpeed / 16, weatherInfo);

                try {
                    collector.emit(new Values(TRAFFIC_KEY_PREFIX.concat(geoHash), mapper.writeValueAsString(record)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("key", "value"));
    }
}
