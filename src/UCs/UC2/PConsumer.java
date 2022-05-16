package UCs.UC2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.serialization.StringDeserializer;

import Entities.EConsumer;
import Entities.ESensorRecord;
import GUI.entities.PConsumerGUI;
import Interfaces.IConsumer;
import Interfaces.IConsumerStats;
import Monitors.MConsumerStats;
import Serializers.SensorRecordDeserializer;

public class PConsumer implements IConsumer<ConsumerRecord<String, ESensorRecord>> {

    private final MConsumerStats<Integer, ESensorRecord> mConsumerStats;
    private final Map<Integer, Long> lastTimestamps;
    private final PConsumerGUI GUI;

    public PConsumer(int nConsumers) {
        this.mConsumerStats = new MConsumerStats<>();
        this.lastTimestamps = new HashMap<>();
        this.GUI = new PConsumerGUI(nConsumers, 0, 0);
    }

    @Override
    public void consume(int id, ConsumerRecord<String, ESensorRecord> element) throws OutOfOrderSequenceException {
        System.out.println("PConsumer received: " + element.toString());
        this.mConsumerStats.count(element.value().getSensorID(), element.value());
        
        int sensorID = element.value().getSensorID();
        Long count = this.lastTimestamps.get(sensorID);
        if(count == null) {
            this.lastTimestamps.put(sensorID, 1L);
        }
        else {
            if(count > element.value().getTimestamp()) {
                throw new OutOfOrderSequenceException("Consumer received element out of order");
            }
            this.lastTimestamps.put(sensorID, count+1);
        }
        this.GUI.addData(element.value(), id);
    }

    public IConsumerStats <Integer, ESensorRecord> getConsumerStats() {
        return this.mConsumerStats;
    }
    
    public static void main(String args[]) {

        final int nConsumers = 6;

        // kafka properties
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorRecordDeserializer.class.getName());
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "0");
        
        List<String> topics = Arrays.asList("sensor");
        EConsumer consumer = new EConsumer(properties, topics, nConsumers);

        final IConsumer<ConsumerRecord<String, ESensorRecord>> processConsumer = new PConsumer(nConsumers);

        for(int i=0;i<nConsumers;i++) {
            final int iFinal = i;
            new Thread(() -> {
                PConsumer.addHook(processConsumer);
                consumer.listen(iFinal, processConsumer);
            }).start();
        }
        
    }

    public static void addHook(IConsumer<ConsumerRecord<String, ESensorRecord>> processConsumer) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    IConsumerStats<Integer, ESensorRecord> consumerStats = ((PConsumer)processConsumer).getConsumerStats();
                    System.out.println(consumerStats.toString());
                    
                    System.out.println("Shutting down ...");
    
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
    }

}
