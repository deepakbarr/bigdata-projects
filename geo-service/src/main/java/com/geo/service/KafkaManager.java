package com.geo.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbarr on 1/7/18.
 */


enum Topics {
    supplydemand_output, traffic_output;
}

public class KafkaManager {


    private static KafkaManager INSTANCE = new KafkaManager();
    Map<String, KafkaPoller> kafkaPollers = new HashMap();
    private String kafkaBroker;

    private KafkaManager() {
    }

    public static KafkaManager get() {
        return INSTANCE;
    }

    public void init(String kafkaBroker) {
        System.out.println("Initialized with kafkabroker : " + kafkaBroker);
        this.kafkaBroker = kafkaBroker;
    }

    public LocationDTO getNextMessage(String topic, String clientId) throws IOException, InterruptedException {

        String key = topic + "." + clientId;
        if (!kafkaPollers.containsKey(key)) {
            System.out.println("Key Does not Exists  = " + key);
            kafkaPollers.put(key, new KafkaPoller(kafkaBroker, topic, clientId));
        }
        else {
            System.out.println("Key Exists  = " + key);
        }

        Map<String, String> records = kafkaPollers.get(key).getNewMessages();
        System.out.println("Number of records received : "+records.size());

        if (topic.equals(Topics.supplydemand_output.toString()))
            return DataUtil.toSupplyDemandGeoPoint(records);
        if (topic.equals(Topics.traffic_output.toString()))
            return DataUtil.toTrafficGeoPoint(records);
        return null;
    }

    public void reset(String topic, String clientId) throws IOException, InterruptedException {
        String key = topic + "." + clientId;
        if (kafkaPollers.containsKey(key)) {
            kafkaPollers.get(key).reset();
            System.out.println("Kafka consumer for the following key is reset :  "+key);
        }
        else {
            System.out.println("Kafka consumer does not exist for key :  "+key);
        }
    }
}



