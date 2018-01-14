#!/bin/bash

#nohup redis-server &
#echo "start redis server"


if [ -z "${REDIS_HOST}" ]
then
    echo "REDIS_HOST is not set"
    exit
fi
echo "REDIS_HOST:${REDIS_HOST}"

nohup redis-commander --redis-host $REDIS_HOST --port 9099 &
echo "started redis commander at 9099 port"