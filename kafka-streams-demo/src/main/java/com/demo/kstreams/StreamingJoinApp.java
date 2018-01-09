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
//public class StreamingJoinApp {
//
//    public static void main(String[] args) {
//        Properties config = new Properties();
//        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-wc-app");
//        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        config.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        config.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//
//        KStreamBuilder builder = new KStreamBuilder();
//
//        KStream<String, Long> userClicksStream = builder.stream("user-clicks-topic");
//        KTable<String, String> userRegionsTable = builder.table("user-regions-topic");
//
//
//        KTable<String, Long> clicksPerRegion = userClicksStream
//                .leftJoin(userRegionsTable, (clicks, region) -> new RegionWithClicks(region == null ? "UNKNOWN" : region, clicks))
//                .map((user, regionWithClicks) -> new KeyValue<>(regionWithClicks.getRegion(), regionWithClicks.getClicks()))
//                .reduceByKey((firstClicks, secondClicks) -> firstClicks + secondClicks,...);
//
//        clicksPerRegion.to("clicks-per-region-topic");
//        KafkaStreams streams = new KafkaStreams(builder, config);
//        streams.start();
//        System.out.println("streams = " + streams.toString());
//        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
//    }
//
//
//}
//
//class RegionWithClicks {
//    private String region;
//    private Long clicks;
//
//    public RegionWithClicks(String region, Long clicks) {
//        this.region = region;
//        this.clicks = clicks;
//    }
//
//    public String getRegion() {
//        return region;
//    }
//
//    public void setRegion(String region) {
//        this.region = region;
//    }
//
//    public Long getClicks() {
//        return clicks;
//    }
//
//    public void setClicks(Long clicks) {
//        this.clicks = clicks;
//    }
//}
