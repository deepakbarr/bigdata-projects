#!/bin/bash

nohup redis-server &
echo "start redis server"
nohup redis-commander --port 9099 &
echo "start redis commander at 9099 port"