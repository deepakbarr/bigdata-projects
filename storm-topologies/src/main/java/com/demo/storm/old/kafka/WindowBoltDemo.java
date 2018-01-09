package com.demo.storm.old.kafka;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dbarr on 12/18/17.
 */
public class WindowBoltDemo extends BaseWindowedBolt {

    private OutputCollector collector;

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }


    @Override
    public void execute(TupleWindow tupleWindow) {
        List<String> words = new ArrayList();
        for (Tuple t : tupleWindow.get()) {
            words.add(t.getString(0));
        }
        collector.emit(new Values("-------> TS = " + new Date() + ",  Total words : " + tupleWindow.get().size() + " , words  = " + words.toString()));
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("window-output"));

    }
}
