#!/bin/bash

if [ -z "${CONFLUENT_HOME}" ]
then
    echo "CONFLUENT_HOME is not set"
    exit
fi
echo "CONFLUENT_HOME:${CONFLUENT_HOME}"

$CONFLUENT_HOME/bin/kafka-topics --zookeeper localhost:2181 --delete --topic demand_topic
$CONFLUENT_HOME/bin/kafka-topics --zookeeper localhost:2181 --delete --topic supply_topic
$CONFLUENT_HOME/bin/kafka-topics --zookeeper localhost:2181 --delete --topic supplydemand_output
$CONFLUENT_HOME/bin/kafka-topics --zookeeper localhost:2181 --delete --topic traffic_output
$CONFLUENT_HOME/bin/kafka-topics --zookeeper localhost:2181 --delete --topic batch_output