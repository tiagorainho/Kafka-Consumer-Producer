package Interfaces;

import java.util.List;

public interface IConsumerStats<K, V> {

    public int getNumberOfReceivedRecords();

    public int getNumberOfRecordsBySensorID(int sensorID);

    public List<V> getOrderedRecords();

    public void count(K key, V value);
}
