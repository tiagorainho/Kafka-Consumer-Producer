package Serializers;

import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.serialization.Deserializer;

import Entities.ESensorRecord;

public class SensorRecordDeserializer implements Deserializer<ESensorRecord> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public ESensorRecord deserialize(String arg0, byte[] data) {
        return SerializationUtils.deserialize(data);
    }

    @Override
    public void close() {
    }

}
