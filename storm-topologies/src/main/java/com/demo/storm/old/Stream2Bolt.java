package com.demo.storm.old;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by dbarr on 12/19/17.
 */
public class Stream2Bolt extends BaseBasicBolt {

    private OutputCollector collector;


    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {

        if (tuple.getString(0).length() == 0)
            return;
        String[] val = tuple.getString(0).split(",");

        if (val.length != 2)
            return;

        System.out.println("-------> Stream 2 ----> " + tuple.getString(0));
        basicOutputCollector.emit(new Values(val[0], val[1]));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("key2", "value2"));

    }

}
