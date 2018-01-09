package com.demo.storm.excercise.batch;

import com.demo.commons.GeoUtil;
import com.demo.storm.excercise.DataHolder;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbarr on 12/18/17.
 */
public class BatchProcessingBolt extends BaseWindowedBolt {

    private OutputCollector collector;

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.MINUTE);
        JSONParser parser = new JSONParser();
        Map<String, DataHolder> dataMap = new HashMap();
        DataHolder dataHolder;
        for (Tuple t : tupleWindow.get()) {
            try {
                JSONObject jsonObject = (JSONObject) parser.parse(t.getString(0));

                double lat = (Double) jsonObject.get("curr_latitude");
                double lon = (Double) jsonObject.get("curr_longitude");

                String geoHash = GeoUtil.toGeohash(lat, lon);

                if (!dataMap.containsKey(geoHash)) {
                    dataMap.put(geoHash, new DataHolder());
                }
                dataHolder = dataMap.get(geoHash);

                String customerId = (String) jsonObject.get("customer_id");
                if (customerId != null) {
                    dataHolder.addDemandRecord(customerId);
                } else {
                    String driverId = (String) jsonObject.get("driver_id");
                    dataHolder.addSupplyRecord(driverId);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (String geoHash : dataMap.keySet()) {
            dataHolder = dataMap.get(geoHash);
            collector.emit(new Values(geoHash, dataHolder.countSupply(), dataHolder.countDemand(), cal.getTime().getTime()));
        }
        System.out.println("Batch Processing Bolt => Rows published => " + dataMap.size());
        System.out.println("Timestamp = " + cal.getTime());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("geohash", "supply", "demand", "timestamp"));
    }
}
