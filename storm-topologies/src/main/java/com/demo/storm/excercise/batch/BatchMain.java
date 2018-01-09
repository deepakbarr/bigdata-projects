package com.demo.storm.excercise.batch;

import com.demo.storm.old.kafka.MyKafkaStormUtil;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.shade.org.eclipse.jetty.util.ajax.JSON;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;

import static com.demo.storm.excercise.Constants.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by dbarr on 12/18/17.
 */
public class BatchMain {


    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {
            System.out.println("Usage : MainClass <broker_string> <zookeeper_string>");
            System.exit(1);
        }

        String BROKERS = args[0];
        String ZK_CONNECT = args[1];

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpout demandKafkaSpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, DEMAND_TOPIC);
        KafkaSpout supplyKafkaSpout = MyKafkaStormUtil.getKafkaSpout(ZK_CONNECT, SUPPLY_TOPIC);

        topologyBuilder.setSpout("demand-spout", demandKafkaSpout, 1);
        topologyBuilder.setSpout("supply-spout", supplyKafkaSpout, 1);

        BaseWindowedBolt batchProcessingBolt = new BatchProcessingBolt()
                .withTumblingWindow(new BaseWindowedBolt.Duration(30, TimeUnit.SECONDS));

        topologyBuilder.setBolt("batch-processing-bolt", batchProcessingBolt).shuffleGrouping("demand-spout").shuffleGrouping("supply-spout");

        EnrichmentBolt enrichmentBolt = new EnrichmentBolt();
        topologyBuilder.setBolt("enrichment-bolt", enrichmentBolt, 1).shuffleGrouping("batch-processing-bolt");
        topologyBuilder.setBolt("batch-kafka-bolt", MyKafkaStormUtil.getKafkaBolt(BROKERS, BATCH_OUTPUT_TOPIC), 1).shuffleGrouping("enrichment-bolt");

        Config conf = new Config();
        conf.setMessageTimeoutSecs(600);
//        conf.setDebug(true);
        conf.setMaxTaskParallelism(1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("batch-topology", conf, topologyBuilder.createTopology());
//        Thread.sleep(10000);
//        cluster.shutdown();

    }
}
