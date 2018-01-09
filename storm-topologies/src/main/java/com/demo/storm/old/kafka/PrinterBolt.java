package com.demo.storm.old.kafka;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * Created by dbarr on 12/18/17.
 */
public class PrinterBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {

        String value = tuple.getStringByField("str");
        System.out.println("---------------->>>  tuple = " + tuple.toString());
        System.out.println("---------------->>>  tuple = " + tuple.getFields().toList().toString());

        String val = "Kafka-Bolt -> ".concat(value).concat(", SourceComponent = " + tuple.getSourceComponent()).toUpperCase();
        tuple.getSourceComponent();
        collector.emit(new Values(val));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("output"));
    }
}
