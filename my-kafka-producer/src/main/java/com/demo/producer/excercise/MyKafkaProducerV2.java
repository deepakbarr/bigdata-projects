package com.demo.producer.excercise;

import com.demo.producer.KafkaUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.time.DateUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by dbarr on 12/22/17.
 */
public class MyKafkaProducerV2 extends KafkaUtils {

    private static final long sleep = 10l;
    private static String kafkaBrokers;
    private static String topic;
    private static String file;
    private static long maxRows;
    public static String ID_KEY;


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        if (args.length != 5) {
            System.out.println("Usage : com.demo.producer.excercise.MyKafkaProducer <data_file> <topic_name> <kafka_brokers> <rows(-1 to load all rows)> <primery_key>");
            System.exit(1);
        }
        file = args[0];
        topic = args[1];
        kafkaBrokers = args[2];
        maxRows = Long.parseLong(args[3]);
        if (maxRows < 0)
            maxRows = Long.MAX_VALUE;
        ID_KEY = args[4];
        new MyKafkaProducerV2().run();
    }

    public void run() throws ExecutionException, InterruptedException {

        System.out.println(String.format("Loading data from %s to %s kafka topic", file, topic));

        Producer<String, String> producer = getKafkaProducer(kafkaBrokers);
        long sent = 0;
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {

                BaseRecord baseRecord;
                if (ID_KEY.equals("driver_id")) {
                    baseRecord = mapper.readValue(sCurrentLine, SupplyBaseRecord.class);
                } else if (ID_KEY.equals("customer_id")) {
                    baseRecord = mapper.readValue(sCurrentLine, DemandBaseRecord.class);
                } else {
                    throw new RuntimeException("Invalid primeryKey  :" + ID_KEY);
                }
                baseRecord.setTimestamp(String.valueOf(System.currentTimeMillis()));
                producer.send(new ProducerRecord<String, String>(topic, mapper.writeValueAsString(baseRecord))).get();
                sent++;
                if (sent % 100 == 0) {
                    Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.MINUTE);
                    System.out.println("Number of records pushed : " + sent + " , Truncated time => " + cal.getTime());
                }
                if (sent >= maxRows)
                    break;
//                Thread.sleep(sleep);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Records pushed to kafka topic = " + sent);
            producer.close();
        }
    }
}
