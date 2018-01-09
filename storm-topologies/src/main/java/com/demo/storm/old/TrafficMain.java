package com.demo.storm.old;

import com.demo.storm.excercise.MyRedisDataMapper;
import com.demo.storm.old.kafka.MyKafkaStormUtil;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;

import java.util.concurrent.TimeUnit;

/**
 * Created by dbarr on 12/18/17.
 */
public class TrafficMain {

    private static final String DEMAND_TOPIC = "demand_topic";
    private static final String SUPPLY_TOPIC = "supply_topic2";
    private static final String OUTPUT_TOPIC = "traffic_output";
//
//    private static final String DEMAND_TOPIC = "test_topic1";
//    private static final String SUPPLY_TOPIC = "test_topic2";
//    private static final String OUTPUT_TOPIC = "test_output_topic";

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass  <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS=args[0];
        String ZK_CONNECT=args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout kafkaSpout1 = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, DEMAND_TOPIC);
        KafkaSpout kafkaSpout2 = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, SUPPLY_TOPIC);

//        topologyBuilder.setSpout("kafka-spout1", kafkaSpout1, 1);
        topologyBuilder.setSpout("kafka-spout2", kafkaSpout2, 1);

//        topologyBuilder.setBolt("stream-1-bolt", new Stream1Bolt()).shuffleGrouping("kafka-spout1");
//        topologyBuilder.setBolt("stream-2-bolt", new Stream2Bolt()).shuffleGrouping("kafka-spout2");

        BaseWindowedBolt trafficCalculator = new TrafficCalculator()
                .withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.MINUTES), new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES));

        topologyBuilder.setBolt("traffic-bolt", trafficCalculator).shuffleGrouping("kafka-spout2");

//        PrinterBolt printerBolt = new PrinterBolt();
//        topologyBuilder.setBolt("printer-bolt", printerBolt, 1).shuffleGrouping("processing-bolt");

        topologyBuilder.setBolt("kafka-bolt", MyKafkaStormUtil.getKafkaBolt(BROKERS, OUTPUT_TOPIC), 1).shuffleGrouping("traffic-bolt");


        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost("localhost").setPort(6379).build();

        RedisStoreMapper storeMapper = new MyRedisDataMapper("TRAFFIC.");
        RedisStoreBolt redisStoreBolt = new RedisStoreBolt(poolConfig, storeMapper);
        topologyBuilder.setBolt("redis-store-bolt", redisStoreBolt).shuffleGrouping("traffic-bolt");

        Config conf = new Config();

        conf.setMessageTimeoutSecs(600);
//        conf.setDebug(true);
        conf.setMaxTaskParallelism(1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("traffic-topology", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();
    }

//    public static void main(String[] args) throws InterruptedException {
//        TopologyBuilder topologyBuilder = new TopologyBuilder();
//        KafkaSpout kafkaSpout1 = MyKafkaStormUtil.getKafkaSpout("storm_topic1");
//        KafkaSpout kafkaSpout2 = MyKafkaStormUtil.getKafkaSpout("storm_topic2");
//
//        topologyBuilder.setSpout("kafka-spout1", kafkaSpout1, 1);
//        topologyBuilder.setSpout("kafka-spout2", kafkaSpout2, 1);
//
//        topologyBuilder.setBolt("stream-1-bolt", new com.demo.storm.old.problem.Stream1Bolt()).shuffleGrouping("kafka-spout1");
//        topologyBuilder.setBolt("stream-2-bolt", new com.demo.storm.old.problem.Stream2Bolt()).shuffleGrouping("kafka-spout2");
//
//
////
//        JoinBolt jbolt = new JoinBolt("stream-1-bolt", "key1")
//                .join("stream-2-bolt", "key2", "stream-1-bolt").select("value1, value2")
//                .withTumblingWindow(new BaseWindowedBolt.Duration(20, TimeUnit.SECONDS));
//
////
////
//
////
//        topologyBuilder.setBolt("join-bolt", jbolt).fieldsGrouping("stream-1-bolt", new Fields("key1", "value1"))
//                .fieldsGrouping("stream-2-bolt", new Fields("key2", "value2"));
//
//        com.demo.storm.old.problem.PrinterBolt printerBolt = new com.demo.storm.old.problem.PrinterBolt();
//        topologyBuilder.setBolt("printer-bolt", printerBolt, 1).shuffleGrouping("join-bolt");
//        topologyBuilder.setBolt("kafka-bolt", MyKafkaStormUtil.getKafkaBolt(), 1).shuffleGrouping("printer-bolt");
//
//
//        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
//                .setHost("localhost").setPort(6379).build();
//        RedisStoreMapper storeMapper = new com.demo.storm.old.problem.MyRedisDataMapper();
//        RedisStoreBolt redisStoreBolt = new RedisStoreBolt(poolConfig, storeMapper);
//        topologyBuilder.setBolt("redis-store-bolt", redisStoreBolt).shuffleGrouping("stream-1-bolt");
//
//        Config conf = new Config();
//
//        conf.setMessageTimeoutSecs(300);
//        conf.setDebug(true);
//        conf.setMaxTaskParallelism(1);
//        LocalCluster cluster = new LocalCluster();
//        cluster.submitTopology("join-topology", conf, topologyBuilder.createTopology());
////        Thread.sleep(10000);
////        cluster.shutdown();
//    }
}
