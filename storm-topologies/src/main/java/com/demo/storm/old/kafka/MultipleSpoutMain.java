package com.demo.storm.old.kafka;

import com.demo.storm.excercise.StormUtil;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by dbarr on 12/18/17.
 */
public class MultipleSpoutMain {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass  <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS = args[0];
        String ZK_CONNECT = args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout kafkaSpout1 = StormUtil.getKafkaSpout(ZK_CONNECT, "storm_topic1");
        KafkaSpout kafkaSpout2 = StormUtil.getKafkaSpout(ZK_CONNECT, "storm_topic2");

        topologyBuilder.setSpout("kafka-spout1", kafkaSpout1, 1);
        topologyBuilder.setSpout("kafka-spout2", kafkaSpout2, 1);

        PrinterBolt printerBolt = new PrinterBolt();

        topologyBuilder.setBolt("printer-bolt", printerBolt, 1).shuffleGrouping("kafka-spout1").shuffleGrouping("kafka-spout2");
        topologyBuilder.setBolt("kafka-bolt", StormUtil.getKafkaBolt(BROKERS, "storm_output"), 1).shuffleGrouping("printer-bolt");

        Config conf = new Config();
        conf.setDebug(true);
        conf.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("multiple-spouts-example", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();
    }
}
