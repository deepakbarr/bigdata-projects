#!/bin/bash

CONF_FILE=$1
curl -X POST -H "Content-Type: application/json" -d @$CONF_FILE "http://localhost:8090/druid/indexer/v1/task"