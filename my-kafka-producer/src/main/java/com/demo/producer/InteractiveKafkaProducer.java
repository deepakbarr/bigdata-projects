package com.demo.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by dbarr on 12/17/17.
 */
public class InteractiveKafkaProducer extends KafkaUtils {

    private static String KAFKA_BROKER = "localhost:9092";

    private static final long sleep = 2000l;
    private static final List<String> exitWords = Arrays.asList("exit", "quit", "EXIT", "QUIT");

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new InteractiveKafkaProducer().run();
    }

    public void run() throws ExecutionException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter topic name");
        String topic = sc.nextLine();

        final Producer<String, String> producer = getKafkaProducer(KAFKA_BROKER);
        while (true) {
            String input = sc.nextLine();
            if (exitWords.contains(input))
                break;
            String[] tokens = input.split(",");
            if (tokens.length != 2) {
                System.err.println(" Bad user input : " + input);
                continue;
            }
            producer.send(new ProducerRecord<String, String>(topic, tokens[0], tokens[1])).get();
            System.out.println("Sent at " + new Date());
            Thread.sleep(sleep);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                producer.close();
            }
        });
    }
}
