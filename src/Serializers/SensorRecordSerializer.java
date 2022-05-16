package Serializers;

import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.serialization.Serializer;

import Entities.ESensorRecord;

public class SensorRecordSerializer implements Serializer<ESensorRecord> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, ESensorRecord data) {
        return SerializationUtils.serialize(data);
    }

    @Override
    public void close() {
    }

    
}
