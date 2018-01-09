package com.demo.storm.old.wordcount;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbarr on 12/17/17.
 */
public class CounterBolt extends BaseBasicBolt {


    private Map<String, Long> map = new HashMap();

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String word = tuple.getStringByField("word");
        if (!map.containsKey(word)) {
            map.put(word, 0l);
        }
        long count = map.get(word);
        map.put(word, count + 1);

//        LOG.info("Count of word: " + word + " = " + count);
        System.out.println("Count of word: " + word + " = " + count);
        collector.emit(new Values(word, count));

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }
}
