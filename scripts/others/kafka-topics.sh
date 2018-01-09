#!/usr/bin/env bash

./bin/kafka-topics --create --zookeeper localhost:2181 --topic demand_topic  --partitions 1 --replication-factor 1

./bin/kafka-topics --create --zookeeper localhost:2181 --topic supply_topic  --partitions 1 --replication-factor 1

./bin/kafka-topics --create --zookeeper localhost:2181 --topic output_topic  --partitions 1 --replication-factor 1




./bin/kafka-topics --create --zookeeper localhost:2181 --topic test_topic1  --partitions 1 --replication-factor 1

./bin/kafka-topics --create --zookeeper localhost:2181 --topic test_topic2  --partitions 1 --replication-factor 1

./bin/kafka-topics --create --zookeeper localhost:2181 --topic test_output_topic  --partitions 1 --replication-factor 1

#kafka console consumer
bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic demand_topic  --from-beginning --property print.key=true --property print.value=true