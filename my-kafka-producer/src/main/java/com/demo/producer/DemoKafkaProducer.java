package com.demo.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by dbarr on 12/16/17.
 */
public class DemoKafkaProducer extends KafkaUtils {

    private static String KAFKA_BROKER = "localhost:9092";

    private static final String topic = "test_topic2";
    private static final long sleep = 1000l;
    private static int seed = 100;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new DemoKafkaProducer().run();
    }

    public void run() throws ExecutionException, InterruptedException {
        final Producer<String, String> producer = getKafkaProducer(KAFKA_BROKER);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                producer.close();
            }
        });

        while (true) {
            producer.send(new ProducerRecord<String, String>(topic, String.valueOf(seed++))).get();
            System.out.println("Message sent");
            Thread.sleep(sleep);
        }

//        while (true) {
//            Producer<String, String> producer = getKafkaProducer();
//            Map<String, String> records = getRecords();
//            for (Map.Entry<String, String> entry : records.entrySet()) {
//                producer.send(new ProducerRecord<String, String>(topic, entry.getKey(), entry.getValue())).get();
//                System.out.println(String.format("Message sent : Key = %s, Value = %s", entry.getKey(), entry.getValue()));
//                Thread.sleep(sleep);
//            }
//            producer.close();
//        }
}

    private Map<String, String> getRecords() {
        Map<String, String> records = new HashMap();
        records.put("A", "Apple");
//        records.put("2", "Bat");
//        records.put("3", "Cat");
        return records;

    }
}
