package com.demo.storm.excercise;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.storm.kafka.*;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.spout.SchemeAsMultiScheme;

import java.util.Properties;
import java.util.UUID;

import static com.demo.storm.excercise.Constants.SUPPLY_DEMAND_KEY_PREFIX;

/**
 * Created by dbarr on 12/18/17.
 */
public class StormUtil {

    public static KafkaSpout getKafkaSpout(String zkString, String topic) {
        BrokerHosts hosts = new ZkHosts(zkString);

        SpoutConfig kafkaSpoutConfig = new SpoutConfig(hosts, topic, "/" + topic,
                UUID.randomUUID().toString());
        kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.ignoreZkOffsets = true;
        kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new KafkaSpout(kafkaSpoutConfig);
    }


    public static KafkaBolt<String, String> getKafkaBolt(String brokers, String topic) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, topic);
        return new KafkaBolt<String, String>()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector(topic))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<>("key", "value"));

    }

    public static RedisStoreBolt getRedisStoreBolt(String keyPrefix) {

        String host = System.getenv("REDIS_HOST");
        String port = System.getenv("REDIS_PORT");

        if (null == host || null == port) {
            System.out.println("Environment variable REDIS.HOST and REDIS.PORT should be defined.");
            System.exit(1);
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost(host).setPort(Integer.parseInt(port)).build();
        RedisStoreMapper storeMapper = new MyRedisDataMapper(keyPrefix);
        return new RedisStoreBolt(poolConfig, storeMapper);
    }
}
