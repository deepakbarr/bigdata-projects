package com.demo.storm.excercise.v2.supplydemand;

import com.demo.storm.excercise.Constants;
import com.demo.storm.excercise.StormUtil;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.bolt.JoinBolt;
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
        topologyBuilder.setSpout("kafka-demand", demandSpout, 1);
        topologyBuilder.setSpout("kafka-supply", supplySpout, 1);

        BaseWindowedBolt demandBolt = new DemandBolt()
                .withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.MINUTES), new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES));

        BaseWindowedBolt supplyBolt = new SupplyBolt()
                .withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.MINUTES), new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES));

        topologyBuilder.setBolt("demand-bolt", demandBolt).shuffleGrouping("kafka-demand");
        topologyBuilder.setBolt("supply-bolt", supplyBolt).shuffleGrouping("kafka-supply");

        JoinBolt joinBolt = new JoinBolt("demand-bolt", "geohash")
                .join("supply-bolt", "geohash", "demand-bolt")
                .select("demand-bolt:geohash, demand_count, supply_count")
                .withTumblingWindow(new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES)).withTimestampField("ts");

        topologyBuilder.setBolt("supply-demand-joiner", joinBolt, 1).shuffleGrouping("demand-bolt").shuffleGrouping("supply-bolt");

        RatioCalculatorBolt ratioCalculatorBolt = new RatioCalculatorBolt();
        topologyBuilder.setBolt("ratio-calculator", ratioCalculatorBolt, 1).shuffleGrouping("supply-demand-joiner");

        topologyBuilder.setBolt("kafka-bolt", StormUtil.getKafkaBolt(BROKERS, SUPPLY_DEMAND_OUTPUT_TOPIC), 1).shuffleGrouping("ratio-calculator");
        RedisStoreBolt redisStoreBolt = StormUtil.getRedisStoreBolt(Constants.SUPPLY_DEMAND_KEY_PREFIX);
        topologyBuilder.setBolt("redis-store-bolt", redisStoreBolt).shuffleGrouping("ratio-calculator");

        Config conf = new Config();
        conf.setMessageTimeoutSecs(600);
//        conf.setDebug(true);
        conf.setMaxTaskParallelism(4);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("realtime-v2-topology", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();
    }
}
