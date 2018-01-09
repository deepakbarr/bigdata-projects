package com.demo.storm.old.wordcount;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * Created by dbarr on 12/17/17.
 */
public class WordCountMain {

    public static void main(String[] args) throws InterruptedException {

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new InputSpout(), 5);
        builder.setBolt("split", new SplitBolt(), 8).shuffleGrouping("spout");
        builder.setBolt("count", new CounterBolt(), 12).fieldsGrouping("split", new Fields("word"));

        Config conf = new Config();
        conf.setDebug(true);
        conf.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("word-count", conf, builder.createTopology());
        Thread.sleep(10000);
        cluster.shutdown();
    }
}
