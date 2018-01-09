#!/bin/bash -ex

BIN_DIR=/Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/binaries
rm -rf $BIN_DIR

mkdir -p $BIN_DIR
cp /Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/geo-service/target/geo-service-1.0-SNAPSHOT.jar $BIN_DIR
cp /Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/storm-topologies/target/storm-topologies-1.0-SNAPSHOT-jar-with-dependencies.jar $BIN_DIR
cp /Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/my-kafka-producer/target/my-kafka-producer-1.0-SNAPSHOT-jar-with-dependencies.jar $BIN_DIR