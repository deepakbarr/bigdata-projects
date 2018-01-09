package com.demo.kstreams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import java.util.Properties;

/**
 * Created by dbarr on 12/17/17.
 */
public class TumblingWindowApp {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-tumbling-window-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> kStream = builder.stream("demo_topic3");


        KTable<Windowed<String>, Long> counts = kStream.groupByKey().count(TimeWindows.of(10 * 1000).until(10*1000));
        counts.toStream((k, v) -> k.key()).to(Serdes.String(), Serdes.Long(), "count_topic");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.cleanUp();
        streams.start();

        System.out.println("streams = " + streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
