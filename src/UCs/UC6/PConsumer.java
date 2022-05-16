package UCs.UC6;

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

    class Average {

        private double count;
        private double n;

        public Average() {
            this.count = 0;
            this.n = 0;
        }

        public void add(Double value) {
            this.count += value;
            this.n++;
        }

        public Double getMean() {
            return this.count / this.n;
        }

    }

    private final MConsumerStats<Integer, ESensorRecord> mConsumerStats;
    private final PConsumerGUI GUI;

    private final Map<Integer, Average> averages;
    
    public PConsumer(int numConsumers, int nClusters) {
        this.mConsumerStats = new MConsumerStats<>();
        this.GUI = new PConsumerGUI(numConsumers, nClusters, 2);
        this.averages = new HashMap<>();
    }

    @Override
    public void consume(int id, ConsumerRecord<String, ESensorRecord> record) throws OutOfOrderSequenceException {
        // System.out.println("PConsumer received: " + record.toString());
        ESensorRecord sensorRecord = record.value();

        // this.mConsumerStats.count(sensorRecord.getSensorID(), sensorRecord);

        Average avr = this.averages.get(sensorRecord.getSensorID());
        if(avr == null) {
            avr = new Average();
            this.averages.put(sensorRecord.getSensorID(), avr);
        }
        avr.add(sensorRecord.getValue());

        this.GUI.addData(sensorRecord, id);
        this.GUI.updateAverage(avr.getMean(), sensorRecord.getSensorID());
    }

    public IConsumerStats <Integer, ESensorRecord> getConsumerStats() {
        return this.mConsumerStats;
    }
    
    public static void main(String args[]) {

        final int nConsumers = 3;
        final int nClusters = 1;

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
