package com.demo.storm.excercise.v2.batch;

import com.demo.storm.old.kafka.MyKafkaStormUtil;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.bolt.JoinBolt;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TimestampExtractor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.TimeUnit;

import static com.demo.storm.excercise.Constants.*;

/**
 * Created by dbarr on 12/18/17.
 */
public class BatchMainTimeStampExtractor {

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS = args[0];
        String ZK_CONNECT = args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout demandSpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, DEMAND_TOPIC);
        KafkaSpout supplySpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, SUPPLY_TOPIC);
        topologyBuilder.setSpout("kafka-demand", demandSpout, 1);
        topologyBuilder.setSpout("kafka-supply", supplySpout, 1);

        BaseWindowedBolt demandBolt = new DemandBolt()
                .withTumblingWindow(new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES)).withTimestampExtractor(new TimestampExtractor() {
                    @Override
                    public long extractTimestamp(Tuple tuple) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(tuple.getString(0));
                            return Long.parseLong((String)jsonObject.get("timestamp"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                }).withLag(BaseWindowedBolt.Duration.seconds(1));

        BaseWindowedBolt supplyBolt = new SupplyBolt()
                .withTumblingWindow(new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES)).withTimestampExtractor(new TimestampExtractor() {
                    @Override
                    public long extractTimestamp(Tuple tuple) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(tuple.getString(0));
                            return Long.parseLong((String)jsonObject.get("timestamp"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                }).withLag(BaseWindowedBolt.Duration.seconds(1));

        topologyBuilder.setBolt("demand-bolt", demandBolt).shuffleGrouping("kafka-demand");
        topologyBuilder.setBolt("supply-bolt", supplyBolt).shuffleGrouping("kafka-supply");

        JoinBolt joinBolt = new JoinBolt("demand-bolt", "geohash_ts")
                .join("supply-bolt", "geohash_ts", "demand-bolt")
                .select("demand-bolt:geohash_ts, demand_count, supply_count")
                .withTumblingWindow(new BaseWindowedBolt.Duration(1, TimeUnit.MINUTES)).withTimestampField("ts");

        topologyBuilder.setBolt("supply-demand-joiner", joinBolt, 1).shuffleGrouping("demand-bolt").shuffleGrouping("supply-bolt");

        EnrichmentBolt enrichmentBolt = new EnrichmentBolt();
        topologyBuilder.setBolt("enrichment-bolt", enrichmentBolt, 1).shuffleGrouping("supply-demand-joiner");
        topologyBuilder.setBolt("batch-kafka-bolt", MyKafkaStormUtil.getKafkaBolt(BROKERS, BATCH_OUTPUT_TOPIC), 1).shuffleGrouping("enrichment-bolt");

        Config conf = new Config();
        conf.setMessageTimeoutSecs(600);
//        conf.setDebug(true);
        conf.setMaxTaskParallelism(4);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("batch-v2-topology", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();

    }
}
