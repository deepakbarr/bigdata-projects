package com.demo.storm.old.problem;

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


        System.out.println("Fields  = " + tuple.getFields().toList().toString());
        System.out.println("Tuple  = " + tuple);

//        String value = "[K = " + tuple.getStringByField("key") + ", V = " + tuple.getStringByField("value") + "]";
//        String val = "Kafka-Bolt -> ".concat(value).concat(", SourceComponent = " + tuple.getSourceComponent()).toUpperCase();
//        tuple.getSourceComponent();
        collector.emit(new Values("Name = " + tuple.getString(0) + ", Gender = " + tuple.getString(1)));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("output"));
    }
}
