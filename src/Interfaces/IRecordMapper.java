package Interfaces;

import org.apache.kafka.clients.producer.ProducerRecord;

import Entities.ESensorRecord;

public interface IRecordMapper {
    
    public ProducerRecord<String,ESensorRecord> constructProducer(ESensorRecord record);
}
