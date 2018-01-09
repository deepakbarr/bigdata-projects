#!/bin/bash


if [ -z "${CONFLUENT_HOME}" ]
then
    echo "CONFLUENT_HOME is not set"
    exit
fi
echo "CONFLUENT_HOME:${CONFLUENT_HOME}"


$CONFLUENT_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --topic supply_topic --partitions 1 --replication-factor 1
$CONFLUENT_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --topic demand_topic --partitions 1 --replication-factor 1
$CONFLUENT_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --topic traffic_output --partitions 1 --replication-factor 1
$CONFLUENT_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --topic supplydemand_output --partitions 1 --replication-factor 1
$CONFLUENT_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --topic batch_output --partitions 1 --replication-factor 1
