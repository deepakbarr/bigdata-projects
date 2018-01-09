#!/bin/bash


if [ -z "${BINARIES_HOME}" ]
then
    echo "BINARIES_HOME is not set"
    exit
fi
echo "BINARIES_HOME:${BINARIES_HOME}"

DATA_FILE=$DATA_DIR/supply_data_traffic_v2.csv
KAFKA_TOPIC=supply_topic

#Numbers of rows to read from file and send to Kafka, -1 means all rows
NUM_ROWS=-1

java -cp $BINARIES_HOME/my-kafka-producer-1.0-SNAPSHOT-jar-with-dependencies.jar com.demo.producer.excercise.MyKafkaProducer $DATA_FILE $KAFKA_TOPIC $KAFKA_BROKER $NUM_ROWS
