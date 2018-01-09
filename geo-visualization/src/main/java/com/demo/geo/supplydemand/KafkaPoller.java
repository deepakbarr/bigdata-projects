package com.demo.geo.supplydemand;

import com.demo.commons.pojo.OutputRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.util.*;

/**
 * Created by dbarr on 12/22/17.
 */
public class KafkaPoller {

    private ObjectMapper mapper = new ObjectMapper();
    private final static String TOPIC = "supplydemand_output";

//    private final static String BOOTSTRAP_SERVERS = "52.39.226.4:9092";

    private final static String BOOTSTRAP_SERVERS = "13.229.47.198:9092";

    private Consumer<String, String> consumer;

    public KafkaPoller() {
        init();
    }

    private void init() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaPoller11");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 2);


//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Create the consumer using props.
        final Consumer<String, String> consumer =
                new KafkaConsumer(props);
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));
        consumer.poll(0);

        TopicPartition tp = new TopicPartition(TOPIC, 0);
        consumer.seekToBeginning(Arrays.asList(tp));
        System.out.println("consumer.position(tp) = " + consumer.position(tp));
        this.consumer = consumer;
    }


    public Map<String, OutputRecord> runConsumer() throws InterruptedException, IOException {
        Map<String, OutputRecord> records = new HashMap();
        final ConsumerRecords<String, String> consumerRecords = consumer.poll(500);
        if (consumerRecords.count() >= 0) {
            for (ConsumerRecord<String, String> record : consumerRecords) {
                String key = record.key();
                String json = record.value();
                OutputRecord br = mapper.readValue(json, OutputRecord.class);
                records.put(key, br);
            }
        }
        consumer.commitAsync();
        Thread.sleep(1000);
        System.out.println("Polled records = " + records.size());
        return records;
    }

    public void close() {
        consumer.close();
    }

}
