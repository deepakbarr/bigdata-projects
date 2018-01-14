package com.demo.storm.excercise.supplydemand;

import com.demo.storm.excercise.Constants;
import com.demo.storm.excercise.StormUtil;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;

import java.util.concurrent.TimeUnit;

import static com.demo.storm.excercise.Constants.*;

/**
 * Created by dbarr on 12/18/17.
 */
public class AppMain {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass  <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS = args[0];
        String ZK_CONNECT = args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout demandSpout = StormUtil.getKafkaSpout(ZK_CONNECT, DEMAND_TOPIC);
        KafkaSpout supplySpout = StormUtil.getKafkaSpout(ZK_CONNECT, SUPPLY_TOPIC);

        topologyBuilder.setSpout("demand-spout", demandSpout, 1);
        topologyBuilder.setSpout("supply-spout", supplySpout, 1);

        BaseWindowedBolt processingBolt = new ProcessingBolt()
                .withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.MINUTES), new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES));

        topologyBuilder.setBolt("processing-bolt", processingBolt).shuffleGrouping("demand-spout").shuffleGrouping("supply-spout");

        EnrichmentBolt enrichmentBolt = new EnrichmentBolt();
        topologyBuilder.setBolt("enrichment-bolt", enrichmentBolt, 1).shuffleGrouping("processing-bolt");
        topologyBuilder.setBolt("kafka-bolt", StormUtil.getKafkaBolt(BROKERS, SUPPLY_DEMAND_OUTPUT_TOPIC), 1).shuffleGrouping("enrichment-bolt");

        RedisStoreBolt redisStoreBolt = StormUtil.getRedisStoreBolt(Constants.SUPPLY_DEMAND_KEY_PREFIX);
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


}
