#!/bin/bash

arg1=$1
ID="${arg1:-1}"

nServers=1

case $ID in
   1) # UC1
   
   ;;
   2) # UC 2
      nServers=3
   ;;

   3) # UC 3
      nServers=3
   ;;
   4) # UC 4
      nServers=3
   ;;
   5) # UC 5
      nServers=3
   ;;
   6) # UC 6
   ;;

esac

# ----------------------------------------------------------------------
# stop
./clean.sh $ID $nServers

cd ../libs/kafka_2.13-3.1.0

# start
bin/zookeeper-server-start.sh config/zookeeper.properties &
sleep 5

for (( i=0; i<$nServers; i++ ))
do
   bin/kafka-server-start.sh ../../src/UCs/UC$ID/config/server$i.properties &
done

sleep 5

# create topic
bin/kafka-topics.sh --create --topic sensor --bootstrap-server localhost:9092


wait