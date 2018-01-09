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
public class MyKafkaProducer extends KafkaUtils {

    private static final long sleep = 10l;
    private static String kafkaBrokers;
    private static String topic;
    private static String file;
    private static long maxRows;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        if (args.length != 4) {
            System.out.println("Usage : com.demo.producer.excercise.MyKafkaProducer <data_file> <topic_name> <kafka_brokers> <rows(-1 to load all rows)>");
            System.exit(1);
        }
        file = args[0];
        topic = args[1];
        kafkaBrokers = args[2];
        maxRows = Long.parseLong(args[3]);
        if (maxRows < 0)
            maxRows = Long.MAX_VALUE;
        new MyKafkaProducer().run();
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
                if (sent % 100 == 0) {
                    System.out.println("Number of records pushed : " + sent);
                }
                if (sent >= maxRows)
                    break;
//                Thread.sleep(sleep);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Records pushed to kafka topic = " + sent);
            producer.close();
        }
    }
}
