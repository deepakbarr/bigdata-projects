package com.demo.storm.old.kafka;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by dbarr on 12/18/17.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass  <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS=args[0];
        String ZK_CONNECT=args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout kafkaSpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, "storm_topic");
        topologyBuilder.setSpout("kafka-spout", kafkaSpout, 1);

//        topologyBuilder.setBolt("window-bolt", new WindowBoltDemo().withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.DAYS.SECONDS),
//                new BaseWindowedBolt.Duration(1, TimeUnit.DAYS.SECONDS)), 1).shuffleGrouping("kafka-spout");
//
        topologyBuilder.setBolt("print-bolt", new PrinterBolt(), 1).shuffleGrouping("kafka-spout");
        topologyBuilder.setBolt("kafka-bolt", MyKafkaStormUtil.getKafkaBolt(BROKERS, "storm_output"), 1).shuffleGrouping("print-bolt");
        Config conf = new Config();
//        conf.setDebug(true);
        conf.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("kafka-example", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();
    }
}
