#!/bin/bash

arg1=$1
ID="${arg1:-1}"
printf " ---------------- \n"
printf " Using Use Case "$ID"\n"
printf " ---------------- \n"

# PConsumer
cd /Users/tiagorainho/Desktop/University/Masters/2/AS/Assignment2/project/KafkaUseCases ; /usr/bin/env /opt/homebrew/Cellar/openjdk/17.0.2/libexec/openjdk.jdk/Contents/Home/bin/java -XX:+ShowCodeDetailsInExceptionMessages @/var/folders/hc/_v2mvwt16dzdlj93_n2gfcg00000gn/T/cp_541xg4f31qfdxeaz6ppg6ujq4.argfile UCs.UC$ID.PConsumer
