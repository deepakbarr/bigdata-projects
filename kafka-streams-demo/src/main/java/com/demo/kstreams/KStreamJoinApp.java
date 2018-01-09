package com.demo.kstreams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;

/**
 * Created by dbarr on 12/16/17.
 */
public class KStreamJoinApp {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-joining-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> userIds = builder.stream("userid_topic");
        KStream<String, String> userInfo = builder.stream("userinfo_topic");

        KStream<String, String> userEnriched = userIds.join(userInfo, (userIdRecord, userInfoRecord) -> "userId : " + userIdRecord + ", username :" + userInfoRecord,
                JoinWindows.of(60000));

        userEnriched.to("userenriched_topic");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.cleanUp();
        streams.start();
        System.out.println("streams = " + streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
