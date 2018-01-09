#!/bin/bash

display_usage() {
	echo -e "Usage:\n$0 <topology_main_class>\n"
}

if [  $# -lt 1 ]; then
	display_usage
	exit 1
fi

if [ -z "${BINARIES_HOME}" ]
then
    echo "BINARIES_HOME is not set"
    exit
fi
echo "BINARIES_HOME:${BINARIES_HOME}"



MAIN_CLASS=$1
ZK_STRING=localhost:2181

java -cp $BINARIES_HOME/storm-topologies-1.0-SNAPSHOT-jar-with-dependencies.jar $MAIN_CLASS $KAFKA_BROKER $ZK_STRING
