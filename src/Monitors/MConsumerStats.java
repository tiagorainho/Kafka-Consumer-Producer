package Monitors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.mutable.MutableInt;

import Interfaces.IConsumerStats;

public class MConsumerStats<K, V> implements IConsumerStats<K,V> {

    private final ReentrantLock rl;
    private Map<K, MutableInt> counts;
    private List<V> entries;
    
    
    public MConsumerStats() {
        this.rl = new ReentrantLock();
        this.counts = new HashMap<>();
        this.entries = new LinkedList<>();
    }

    public int getNumberOfReceivedRecords() {
        return this.entries.size();
        // return this.counts.values().stream().mapToInt(MutableInt::intValue).sum();
    }

    public int getNumberOfRecordsBySensorID(int sensorID) {
        return this.counts.get(sensorID).intValue();
    }

    public List<V> getOrderedRecords() {
        return this.entries;
    }

    public void count(K key, V value) {
        rl.lock();

        MutableInt count = this.counts.get(key);
        if(count == null)
            this.counts.put(key, new MutableInt(1));
        else
            count.increment();
        
        // add in the heal, therefore guarantee complexity O(N) for the last N entries
        this.entries.add(0, value);

        rl.unlock();

        // System.out.println("Consumido: " + this.counts.toString());
    }

    @Override
    public String toString() {
        return String.format(
            "Total number of records: %d\n" +
            "Records by SensorID: %s",

            this.getNumberOfReceivedRecords(),
            this.counts.toString());
    }


}
