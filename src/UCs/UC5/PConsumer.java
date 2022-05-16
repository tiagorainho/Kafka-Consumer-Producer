package UCs.UC5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private final Map<Integer, Map<Integer, ESensorRecord>> recordsByCluster;
    private final Map<Integer, List<Double>> minMax;
    
    public PConsumer(int numConsumers, int nClusters) {
        this.mConsumerStats = new MConsumerStats<>();
        this.GUI = new PConsumerGUI(numConsumers, nClusters, 1);
        this.recordsByCluster = new HashMap<>();
        this.minMax = new HashMap<>();
        for(int i=0;i<nClusters;i++) {
            this.recordsByCluster.put(i, new HashMap<Integer, ESensorRecord>());
        }
    }

    @Override
    public void consume(int id, ConsumerRecord<String, ESensorRecord> record) throws OutOfOrderSequenceException {
        // System.out.println("PConsumer received: " + record.toString());
        // this.mConsumerStats.count(record.value().getSensorID(), record.value());

        boolean containsAll = true;
        ESensorRecord sensorRecord = record.value();
        this.recordsByCluster.get((int)(id/3)).put(sensorRecord.getTimestamp(), sensorRecord);
        ESensorRecord sensorRecordAux;
        for(Map<Integer, ESensorRecord> sensorRecordsList: this.recordsByCluster.values()) {
            sensorRecordAux = sensorRecordsList.get(sensorRecord.getTimestamp());
            if(sensorRecordAux == null) {
                containsAll = false;
                break;
            }
        }
        if(containsAll) {
            // voting replication tactic
            List<Double> values = new ArrayList<>();
            for(Map<Integer, ESensorRecord> sensorRecordsList: this.recordsByCluster.values()) {
                values.add(sensorRecordsList.get(sensorRecord.
                getTimestamp()).getValue());
            }
            Entry<Double, Long> mostFrequent = values.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
                
            if(mostFrequent != null) {
                // update min max
                List<Double> minMaxAux = this.minMax.get(sensorRecord.getSensorID());
                if(minMaxAux == null) {
                    List<Double> auxLst = new ArrayList<>(2);
                    auxLst.add(0, null);
                    auxLst.add(1, null);
                    this.minMax.put(sensorRecord.getSensorID(), auxLst);
                    minMaxAux = this.minMax.get(sensorRecord.getSensorID());
                }
                Double min = minMaxAux.get(0);
                Double max = minMaxAux.get(1);
                Double temperature = mostFrequent.getKey();
                if(min == null || temperature < min)
                    minMaxAux.set(0, temperature);
                if(max == null || temperature > max)
                    minMaxAux.set(1, temperature);
                
                this.GUI.updateMinMax(minMaxAux, sensorRecord.getSensorID());
            }
        }

        this.GUI.addData(sensorRecord, id);
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
