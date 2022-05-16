package UCs.UC1;

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
    private int lastIndex;

    public PConsumer() {
        this.mConsumerStats = new MConsumerStats<>();
        this.GUI = new PConsumerGUI(1, 0, 0);
        this.lastIndex = 0;
    }


    @Override
    public void consume(int id, ConsumerRecord<String, ESensorRecord> element) throws OutOfOrderSequenceException {
        System.out.println("PConsumer received: " + element.toString());
        this.mConsumerStats.count(element.value().getSensorID(), element.value());
        
        if(this.lastIndex > element.value().getTimestamp()) {
            throw new OutOfOrderSequenceException("Consumer received element out of order");
        }
        this.lastIndex = element.value().getTimestamp();
        this.GUI.addData(element.value(), id);
    }

    public IConsumerStats <Integer, ESensorRecord> getConsumerStats() {
        return this.mConsumerStats;
    }
    
    public static void main(String args[]) {


        // kafka properties
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorRecordDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "0");

        
        List<String> topics = Arrays.asList("sensor");
        EConsumer consumer = new EConsumer(properties, topics, 1);

        IConsumer<ConsumerRecord<String, ESensorRecord>> kafkaConsumer = new PConsumer();

        // print when ends
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    IConsumerStats<Integer, ESensorRecord> consumerStats = ((PConsumer)kafkaConsumer).getConsumerStats();
                    System.out.println(consumerStats.toString());
                    
                    Thread.sleep(200);
                    System.out.println("Shutting down ...");
    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
        
        consumer.listen(0, kafkaConsumer);
        
    }

}
