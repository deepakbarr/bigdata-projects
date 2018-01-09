#!/bin/bash

if [ -z "${CONFLUENT_HOME}" ]
then
    echo "CONFLUENT_HOME is not set"
    exit
fi
echo "CONFLUENT_HOME:${CONFLUENT_HOME}"

$CONFLUENT_HOME/bin/kafka-run-class kafka.tools.GetOffsetShell --broker-list $KAFKA_BROKER  --time -1 --topic supply_topic
$CONFLUENT_HOME/bin/kafka-run-class kafka.tools.GetOffsetShell --broker-list $KAFKA_BROKER  --time -1 --topic demand_topic
$CONFLUENT_HOME/bin/kafka-run-class kafka.tools.GetOffsetShell --broker-list $KAFKA_BROKER  --time -1 --topic traffic_output
$CONFLUENT_HOME/bin/kafka-run-class kafka.tools.GetOffsetShell --broker-list $KAFKA_BROKER  --time -1 --topic supplydemand_output
$CONFLUENT_HOME/bin/kafka-run-class kafka.tools.GetOffsetShell --broker-list $KAFKA_BROKER  --time -1 --topic batch_output