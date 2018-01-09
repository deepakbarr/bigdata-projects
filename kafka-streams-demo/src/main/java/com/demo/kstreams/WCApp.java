//package com.demo.kstreams;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.Serde;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.KStreamBuilder;
//import org.apache.kafka.streams.kstream.KTable;
//
//import java.util.Arrays;
//import java.util.Properties;
//
///**
// * Created by dbarr on 12/15/17.
// */
//public class WCApp {
//
//    public static void main(final String[] args) throws Exception {
//        Properties config = new Properties();
//        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
//        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        final Serde stringSerde = Serdes.String();
//        final Serde longSerde = Serdes.Long();
//
//        KStreamBuilder builder = new KStreamBuilder();
//        KStream<String, String> inputStreamData = builder.stream(stringSerde, stringSerde, "words_topic");
//
//        KStream<String, Long> processedStream = inputStreamData
//                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
//                .map((key, word) -> new KeyValue<>(word, word))
//                .countByKey("Counts")
//                .toStream();
//
//        processedStream.to(stringSerde, longSerde, "count_topic2");
//
//        KafkaStreams streams = new KafkaStreams(builder, config);
//        streams.start();
//        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
//    }
//}
