package Entities;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import Interfaces.IConsumer;


public class EConsumer {

    private List<KafkaConsumer<String, ESensorRecord>> consumers;

    public EConsumer(Properties properties, List<String> topics, int nConsumers, int nClusters) {
        this.consumers = new ArrayList<>(nConsumers);
        if(nClusters == 0) {
            for(int j=0;j<nConsumers;j++) {
                KafkaConsumer<String, ESensorRecord> kafkaConsumer = new KafkaConsumer<>(properties);
                kafkaConsumer.subscribe(topics);
                this.consumers.add(kafkaConsumer);
            }
            return;
        }
        Properties pAux;
        for(int i=0;i<nClusters;i++) {
            // change properties
            pAux = (Properties) properties.clone();
            pAux.put(ConsumerConfig.GROUP_ID_CONFIG, String.valueOf(i+1));

            for(int j=0;j<nConsumers;j++) {
                KafkaConsumer<String, ESensorRecord> kafkaConsumer = new KafkaConsumer<>(pAux);
                kafkaConsumer.subscribe(topics);
                this.consumers.add(kafkaConsumer);
            }
        }
    }

    public EConsumer(Properties properties, List<String> topics, int nConsumers) {
        this(properties, topics, nConsumers, 0);
    }

    public void listen(int id, IConsumer<ConsumerRecord<String, ESensorRecord>> consumer) {
        KafkaConsumer<String, ESensorRecord> kafkaConsumer = this.consumers.get(id);
        Duration duration = Duration.ofMillis(100);
        ConsumerRecords<String, ESensorRecord> records;
        while(true) {
            records = kafkaConsumer.poll(duration);
            for(ConsumerRecord<String, ESensorRecord> record: records) {
                try {
                    consumer.consume(id, record);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }
    }

    public List<KafkaConsumer<String, ESensorRecord>> getConsumers() {
        return this.consumers;
    }
    
}

