//package com.demo.geo;
//
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//
//import java.util.Collections;
//import java.util.Properties;
//
///**
// * Created by dbarr on 12/22/17.
// */
//public class KafkaPollerBK {
//    private final static String TOPIC = "my-example-topic";
//    private final static String BOOTSTRAP_SERVERS =
//            "localhost:9092";
//
//    private Consumer<Long, String> createConsumer() {
//        final Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                BOOTSTRAP_SERVERS);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG,
//                "KafkaPoller");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                StringDeserializer.class.getName());
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                StringDeserializer.class.getName());
//        // Create the consumer using props.
//        final Consumer<Long, String> consumer =
//                new KafkaConsumer(props);
//        // Subscribe to the topic.
//        consumer.subscribe(Collections.singletonList(TOPIC));
//        return consumer;
//    }
//
//    private void runConsumer() throws InterruptedException {
//        final Consumer<Long, String> consumer = createConsumer();
//        final int giveUp = 100;
//        int noRecordsCount = 0;
//        while (true) {
//            final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
//            if (consumerRecords.count() == 0) {
//                noRecordsCount++;
//                if (noRecordsCount > giveUp) break;
//                else continue;
//            }
//            consumer.commitAsync();
//        }
//        consumer.close();
//        System.out.println("DONE");
//    }
//}
