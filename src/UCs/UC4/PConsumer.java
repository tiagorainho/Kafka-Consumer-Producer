package UCs.UC4;

import java.util.Arrays;
import java.util.List;
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
    private final PConsumerGUI GUI;
    
    public PConsumer(int numConsumers, int numClusters) {
        this.mConsumerStats = new MConsumerStats<>();
        this.GUI = new PConsumerGUI(numConsumers, numClusters, 0);
    }

    @Override
    public void consume(int id, ConsumerRecord<String, ESensorRecord> record) throws OutOfOrderSequenceException {
        System.out.println("PConsumer received: " + record.toString());
        this.mConsumerStats.count(record.value().getSensorID(), record.value());
        this.GUI.addData(record.value(), id);
    }

    public IConsumerStats <Integer, ESensorRecord> getConsumerStats() {
        return this.mConsumerStats;
    }
    
    public static void main(String args[]) {

        final int nConsumers = 3;
        final int nClusters = 3;

        // kafka properties
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorRecordDeserializer.class.getName());
        
        List<String> topics = Arrays.asList("sensor");

        EConsumer consumer = new EConsumer(properties, topics, nConsumers, nClusters);

        final IConsumer<ConsumerRecord<String, ESensorRecord>> processConsumer = new PConsumer(nConsumers, nClusters);

        for(int i=0;i<nConsumers*nClusters;i++) {
            final int iFinal = i;
            new Thread(() -> {
                // print when ends
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        IConsumerStats<Integer, ESensorRecord> consumerStats = ((PConsumer)processConsumer).getConsumerStats();
                        System.out.println(consumerStats.toString());
                        System.out.println("Shutting down ...");
                    }
                });
                consumer.listen(iFinal, processConsumer);
            }).start();
        }
        
        
        
    }

}
