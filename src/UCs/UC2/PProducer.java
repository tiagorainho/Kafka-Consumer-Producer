package UCs.UC2;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import Entities.EProducer;
import Entities.ESensorRecord;
import GUI.entities.PProducerGUI;
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
    public void consume(int id, ESensorRecord element) {
        System.out.println("PProducer receives: " + element.toString());
        this.mConsumerStats.count(element.getSensorID(), element);
        this.GUI.addData(element, id);
    }
    
    public static void main(String args[]) {

        // fifo for thread communication
        MFifo<String> fifo = new MFifo<>(Integer.MAX_VALUE);

        IConsumer<ESensorRecord> processConsumer = new PProducer();

        // start tcp server connections to fill the fifo
        int portNumber = 999;
        new TProducerTcpReader(portNumber, fifo).start();
        
        // kafka properties
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SensorRecordSerializer.class.getName());

        properties.put(ProducerConfig.LINGER_MS_CONFIG, "0");
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.RETRIES_CONFIG, "100");


        // start producers
        for(int i=0;i<6;i++) {
            final int iFinal = i;
            new Thread(() -> {
                while(true) {
                    EProducer producer = new EProducer(iFinal, properties, fifo);
                    producer.asyncronousSend(
                        fifo,
                        (sensorRecord) -> new ProducerRecord<String,ESensorRecord>("sensor", String.valueOf(sensorRecord.getSensorID()), sensorRecord),
                        processConsumer
                    );
                }
            }).start();
        }
    }

}
