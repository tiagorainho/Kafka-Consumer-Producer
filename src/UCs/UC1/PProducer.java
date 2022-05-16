package UCs.UC1;


import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import GUI.entities.PProducerGUI;
import Entities.EProducer;
import Entities.ESensorRecord;
import Interfaces.IConsumer;
import Monitors.MConsumerStats;
import Monitors.MFifo;
import Serializers.SensorRecordSerializer;
import Threads.TProducerTcpReader;

public class PProducer implements IConsumer<ESensorRecord> {

    private final MConsumerStats<Integer, ESensorRecord> mConsumerStats;
    private final PProducerGUI GUI;

    public PProducer() {
        this.mConsumerStats = new MConsumerStats<>();
        this.GUI = new PProducerGUI();
    }

    @Override
    public void consume(int id, ESensorRecord record) {
        System.out.println("PProducer receives: " + record.toString());
        this.mConsumerStats.count(record.getSensorID(), record);
        this.GUI.addData(record, id);
    }
    
    public static void main(String args[]) {

        // fifo for thread communication
        MFifo<String> fifo = new MFifo<>(Integer.MAX_VALUE);

        IConsumer<ESensorRecord> processConsumer = new PProducer();

        // start tcp server connections to fill the fifo
        int portNumber = 999;
        TProducerTcpReader reader = new TProducerTcpReader(portNumber, fifo);
        reader.start();
        
        // kafka properties
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorRecordSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "0");


        // start producer
        EProducer producer = new EProducer(0, properties, fifo);
        producer.fireAndForget(
            fifo,
            (sensorRecord) -> new ProducerRecord<String,ESensorRecord>("sensor", 0, null, sensorRecord),
            processConsumer);
        
    }

}
