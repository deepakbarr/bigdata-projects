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
public class SupplyKafkaProducer extends KafkaUtils {
    private static String KAFKA_BROKER = "52.39.226.4:9092";
//    private static String KAFKA_BROKER = "localhost:9092";

    private static final String topic = "supply_topic";
    private static final long sleep = 20l;
    private static final String file = "/Users/dbarr/Garbage/data/supply_data.csv";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new SupplyKafkaProducer().run();
    }

    public void run() throws ExecutionException, InterruptedException {
        Producer<String, String> producer = getKafkaProducer(KAFKA_BROKER);
        long sent = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                producer.send(new ProducerRecord<String, String>(topic, sCurrentLine)).get();
                sent++;

                if (sent == 200)
                    break;
                Thread.sleep(sleep);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Records pushed to kafka = " + sent);
            producer.close();
        }
    }
}
