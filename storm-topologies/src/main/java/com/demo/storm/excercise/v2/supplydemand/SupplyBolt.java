package com.demo.storm.excercise.v2.supplydemand;

import com.demo.commons.GeoUtil;
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

import java.util.*;

/**
 * Created by dbarr on 12/18/17.
 */
public class SupplyBolt extends BaseWindowedBolt {

    private OutputCollector collector;
    long slidingInterval;

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }


    @Override
    public BaseWindowedBolt withWindow(Duration windowLength, Duration slidingInterval) {
        this.slidingInterval = slidingInterval.value;
        return super.withWindow(windowLength, slidingInterval);
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.MINUTE);
        long now = System.currentTimeMillis();
        long windowEnd = now;
        long windowStart = now - slidingInterval;
        System.out.println("Supply windowStart = " + new Date(windowStart));
        System.out.println("Supply windowEnd = " + new Date(windowEnd));

        JSONParser parser = new JSONParser();
        Map<String, Set<String>> dataMap = new HashMap();

        Set<String> driverIds;

        for (Tuple tuple : tupleWindow.get()) {
            try {
                JSONObject jsonObject = (JSONObject) parser.parse(tuple.getString(0));

                double lat = (Double) jsonObject.get("curr_latitude");
                double lon = (Double) jsonObject.get("curr_longitude");
                String geoHash = GeoUtil.toGeohash(lat, lon);

                if (!dataMap.containsKey(geoHash)) {
                    dataMap.put(geoHash, new HashSet());
                }
                driverIds = dataMap.get(geoHash);
                String driverId = (String) jsonObject.get("driver_id");
                driverIds.add(driverId);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (String geoHash : dataMap.keySet()) {
            collector.emit(new Values(geoHash, cal.getTime().getTime(), dataMap.get(geoHash).size()));
        }

        System.out.println("Supply Bolt => Rows published => " + dataMap.size());
        System.out.println("Supply Timestamp = " + cal.getTime());
        System.out.println("Supply Ending window = " + new Date());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("geohash", "ts", "supply_count"));
    }
}
