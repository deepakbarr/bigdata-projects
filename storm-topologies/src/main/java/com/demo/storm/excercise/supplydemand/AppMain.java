package com.demo.storm.excercise.supplydemand;

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

import static com.demo.storm.excercise.Constants.*;

/**
 * Created by dbarr on 12/18/17.
 */
public class AppMain {

    private static final int REDIS_PORT = 6379;

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass  <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS = args[0];
        String ZK_CONNECT = args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout demandSpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, DEMAND_TOPIC);
        KafkaSpout supplySpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, SUPPLY_TOPIC);

        topologyBuilder.setSpout("demand-spout", demandSpout, 1);
        topologyBuilder.setSpout("supply-spout", supplySpout, 1);

        BaseWindowedBolt processingBolt = new ProcessingBolt()
                .withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.MINUTES), new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES));

        topologyBuilder.setBolt("processing-bolt", processingBolt).shuffleGrouping("demand-spout").shuffleGrouping("supply-spout");

        EnrichmentBolt enrichmentBolt = new EnrichmentBolt();
        topologyBuilder.setBolt("enrichment-bolt", enrichmentBolt, 1).shuffleGrouping("processing-bolt");
        topologyBuilder.setBolt("kafka-bolt", MyKafkaStormUtil.getKafkaBolt(BROKERS, SUPPLY_DEMAND_OUTPUT_TOPIC), 1).shuffleGrouping("enrichment-bolt");

        RedisStoreBolt redisStoreBolt = getRedisStoreBolt();
        topologyBuilder.setBolt("redis-store-bolt", redisStoreBolt).shuffleGrouping("enrichment-bolt");

        Config conf = new Config();
        conf.setMessageTimeoutSecs(600);
//        conf.setDebug(true);
        conf.setMaxTaskParallelism(1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("realtime-topology", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();
    }

    public static RedisStoreBolt getRedisStoreBolt() {
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost("localhost").setPort(REDIS_PORT).build();
        RedisStoreMapper storeMapper = new MyRedisDataMapper(SUPPLY_DEMAND_KEY_PREFIX);
        return new RedisStoreBolt(poolConfig, storeMapper);
    }
}
