#!/bin/bash


if [ -z "${BINARIES_HOME}" ]
then
    echo "BINARIES_HOME is not set"
    exit
fi
echo "BINARIES_HOME:${BINARIES_HOME}"


java -cp $BINARIES_HOME/geo-service-1.0-SNAPSHOT.jar com.geo.service.ServiceMain server $CONF_DIR/config.yml