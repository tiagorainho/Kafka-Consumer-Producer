#!/bin/bash

arg1=$1
ID="${arg1:-1}"

arg2=$2
nServers="${arg2:-1}"

cd ../libs/kafka_2.13-3.1.0

for (( i=0; i<$nServers; i++ ))
do
    bin/kafka-server-stop.sh ../../src/UCs/UC$ID/config/server$i.properties
done
bin/zookeeper-server-stop.sh config/zookeeper.properties

for (( i=0; i<$nServers; i++ ))
do
    rm -rf /tmp/kafka-logs-$i
done
rm -rf /tmp/zookeeper

killall java