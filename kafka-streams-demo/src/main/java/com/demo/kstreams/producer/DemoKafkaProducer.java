package com.demo.kstreams.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by dbarr on 12/16/17.
 */
public class DemoKafkaProducer {

    private static final String topic = "userinfo_topic";
    private static final long sleep = 2000l;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = null;
        producer = new KafkaProducer(props);

        producer.send(new ProducerRecord<String, String>(topic, "1", "Apple")).get();
        System.out.println("Sent 1 message");
        Thread.sleep(sleep);


        producer.send(new ProducerRecord<String, String>(topic, "2", "BAT")).get();
        System.out.println("Sent 1 message");
        Thread.sleep(sleep);


        producer.send(new ProducerRecord<String, String>(topic, "3", "CAT")).get();
        System.out.println("Sent 1 message");
        Thread.sleep(sleep);
        producer.close();
    }
}
