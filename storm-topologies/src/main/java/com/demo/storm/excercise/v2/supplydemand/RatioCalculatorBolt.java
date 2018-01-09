package com.demo.storm.excercise.v2.supplydemand;

import com.demo.commons.WeatherUtil;
import com.demo.commons.pojo.OutputRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * Created by dbarr on 12/18/17.
 */

/**
 * Calculates S/D ratio and also adds weather information
 */
public class RatioCalculatorBolt extends BaseBasicBolt {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {

        String geoHash = tuple.getStringByField("demand-bolt:geohash");
        int supply = tuple.getIntegerByField("supply_count");
        int demand = tuple.getIntegerByField("demand_count");
        double ratio = -1d;
        if (demand > 0)
            ratio = (double) supply / demand;
        String weatherInfo = WeatherUtil.getWeatherInfo(geoHash);
        OutputRecord record = new OutputRecord((long) supply, (long) demand, ratio, weatherInfo);
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
