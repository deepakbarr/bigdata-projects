package com.demo.storm.excercise.traffic;

import com.demo.storm.excercise.MyRedisDataMapper;
import com.demo.storm.old.kafka.MyKafkaStormUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import static com.demo.storm.excercise.Constants.*;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by dbarr on 12/18/17.
 */
public class TrafficMain {

    private static final int REDIS_PORT = 6379;

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass  <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS = args[0];
        String ZK_CONNECT = args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout supplySpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, SUPPLY_TOPIC);

        topologyBuilder.setSpout("supply-spout", supplySpout, 1);

        BaseWindowedBolt trafficCalculator = new TrafficCalculator()
                .withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.MINUTES), new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES));

        topologyBuilder.setBolt("traffic-calculator-bolt", trafficCalculator).shuffleGrouping("supply-spout");

        EnrichmentBolt enrichmentBolt = new EnrichmentBolt();
        topologyBuilder.setBolt("enrichment-bolt", enrichmentBolt, 1).shuffleGrouping("traffic-calculator-bolt");

        topologyBuilder.setBolt("kafka-bolt", MyKafkaStormUtil.getKafkaBolt(BROKERS, TRAFFIC_OUTPUT_TOPIC), 1).shuffleGrouping("enrichment-bolt");

        RedisStoreBolt redisStoreBolt = getRedisStoreBolt();
        topologyBuilder.setBolt("redis-store-bolt", redisStoreBolt).shuffleGrouping("enrichment-bolt");

        Config conf = new Config();
        conf.setMessageTimeoutSecs(600);
//        conf.setDebug(true);
        conf.setMaxTaskParallelism(1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("traffic-calculator-topology", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();
    }

    public static RedisStoreBolt getRedisStoreBolt() {
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost("localhost").setPort(REDIS_PORT).build();
        RedisStoreMapper storeMapper = new MyRedisDataMapper(TRAFFIC_KEY_PREFIX);
        return new RedisStoreBolt(poolConfig, storeMapper);
    }
}
