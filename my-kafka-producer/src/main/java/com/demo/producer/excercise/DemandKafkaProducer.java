package com.demo.producer.excercise;

import com.demo.producer.KafkaUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by dbarr on 12/22/17.
 */
public class DemandKafkaProducer extends KafkaUtils {

    private static String kafkaBrokers = "52.39.226.4:9092";
//        private static String KAFKA_BROKER = "localhost:9092";

    private static String topic = "demand_topic";
    private static final long sleep = 10l;
    private static String file = "/Users/dbarr/Garbage/data/demand_data.csv";
    private static long maxRows;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        if (args.length != 4) {
            System.out.println("Usage : package.MainClass <data_file> <topic_name> <kafka_brokers> <rows(-1 to load all rows)>");
            System.exit(1);
        }

        file = args[0];
        topic = args[1];
        kafkaBrokers = args[2];
        maxRows = Long.parseLong(args[3]);
        if (maxRows < 0)
            maxRows = Long.MAX_VALUE;
        new DemandKafkaProducer().run();
    }

    public void run() throws ExecutionException, InterruptedException {

        System.out.println(String.format("Loading data from %s to %s kafka topic", file, topic));

        Producer<String, String> producer = getKafkaProducer(kafkaBrokers);
        long sent = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                producer.send(new ProducerRecord<String, String>(topic, sCurrentLine)).get();
                sent++;
                System.out.println("sent = " + sent);
                if (sent >= maxRows)
                    break;
                Thread.sleep(sleep);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Records pushed to kafka topic = " + sent);
            producer.close();
        }
    }
}
