package com.demo.storm.excercise.v2.batch;

import com.demo.commons.WeatherUtil;
import com.demo.commons.pojo.BatchRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * Created by dbarr on 12/24/17.
 */
public class EnrichmentBolt extends BaseBasicBolt {

    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        int supply = tuple.getIntegerByField("supply_count");
        int demand = tuple.getIntegerByField("demand_count");
        String[] tokens = tuple.getStringByField("demand-bolt:geohash_ts").split("_");
        String geoHash = tokens[0];
        long timestamp = Long.parseLong(tokens[1]);
        String weatherInfo = WeatherUtil.getWeatherInfo(geoHash);
        BatchRecord record = new BatchRecord(geoHash, timestamp, (long) supply, (long) demand, weatherInfo);
        try {
            System.out.println("record sent = " + mapper.writeValueAsString(record));
            collector.emit(new Values(geoHash, mapper.writeValueAsString(record)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("key", "value"));
    }

}
