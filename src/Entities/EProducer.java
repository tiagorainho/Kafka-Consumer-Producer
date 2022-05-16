package Entities;

import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import Interfaces.IConsumer;
import Interfaces.IRecordMapper;
import Monitors.MFifo;

public class EProducer {

    private Producer<String, ESensorRecord> producer;
    private final int id;
    private static final ReentrantLock lock = new ReentrantLock(true);


    public EProducer(int id, Properties properties, MFifo<String> fifo) {
        this.id = id;
        this.producer = new KafkaProducer<>(properties);
    }

    public void syncronousSend(MFifo<String> fifo, IRecordMapper recordMapper, IConsumer<ESensorRecord> processConsumer) {
        String inputString;
        ProducerRecord<String, ESensorRecord> producerRecord = null;
        ESensorRecord sensorRecord = null;
        try {
            while(true) {
            //while(!(fifo.isBlocked() && fifo.isEmpty())) {
                inputString = fifo.pop();
                lock.lock();
                sensorRecord = convertStringToSensorRecord(inputString);
                processConsumer.consume(this.id, sensorRecord);
                // send to kafka
                producerRecord = recordMapper.constructProducer(sensorRecord);
                this.producer.send(producerRecord).get();
                lock.unlock();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            this.producer.close();  
        }
    }

    public void fireAndForget(MFifo<String> fifo, IRecordMapper recordMapper, IConsumer<ESensorRecord> processConsumer) {
        String inputString;
        ProducerRecord<String, ESensorRecord> record = null;
        ESensorRecord sensorRecord = null;
        while(true) {
        // while(!(fifo.isBlocked() && fifo.isEmpty())) {
            inputString = fifo.pop();
            lock.lock();
            // serialize to SensorRecord
            sensorRecord = convertStringToSensorRecord(inputString);
            processConsumer.consume(this.id, sensorRecord);
            // send to kafka
            record = recordMapper.constructProducer(sensorRecord);
            this.producer.send(record);
            lock.unlock();
        }

        // close producer
        // this.producer.close();    
    }

    public void asyncronousSend(MFifo<String> fifo, IRecordMapper recordMapper, IConsumer<ESensorRecord> processConsumer) {
        String inputString;
        ProducerRecord<String, ESensorRecord> record = null;
        ESensorRecord sensorRecord = null;
        while(true) {
        // while(!(fifo.isBlocked() && fifo.isEmpty())) {
            inputString = fifo.pop();
            lock.lock();
            // serialize to SensorRecord
            sensorRecord = convertStringToSensorRecord(inputString);
            processConsumer.consume(this.id, sensorRecord);
            // send to kafka
            record = recordMapper.constructProducer(sensorRecord);
            this.producer.send(record, new ProducerCallback());
            this.producer.flush();
            lock.unlock();
        }

        // close producer
        // this.producer.close();    
    }

    class ProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if(e != null) {
                System.out.println("Asyncronous send failed");
            }
        }
    }
    
    private ESensorRecord convertStringToSensorRecord(String str) {
        String stringParts[] = str.split("-");
        int sensorID = Integer.parseInt(stringParts[0]);
        double value = Double.parseDouble(stringParts[1]);
        int timestamp = Integer.parseInt(stringParts[2]);
        return new ESensorRecord(sensorID, value, timestamp);
    }
}
