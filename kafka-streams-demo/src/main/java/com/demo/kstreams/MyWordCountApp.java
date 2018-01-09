package com.demo.kstreams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by dbarr on 12/15/17.
 */
public class MyWordCountApp {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-wc-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Serde stringSerde = Serdes.String();
        final Serde longSerde = Serdes.Long();
        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> inputStreamData = builder.stream(stringSerde, stringSerde, "words_topic");

        KTable<String, Long> wordCounts = inputStreamData.mapValues(value -> value.toLowerCase())
                .flatMapValues(lowercased -> Arrays.asList(lowercased.split("\\W+")))
                .selectKey((key, value) -> value)
                .groupByKey()
                .count("Counts");

        wordCounts.to(stringSerde, longSerde, "counts_topic");

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.start();

        System.out.println("streams = " + streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
