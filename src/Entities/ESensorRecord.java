package Entities;

import java.io.Serializable;

public class ESensorRecord implements Serializable {

    private int sensorID;
    private double value;
    private int timestamp;

    
    public ESensorRecord(int sensorID, double value, int timestamp) {
        this.sensorID = sensorID;
        this.value = value;
        this.timestamp = timestamp;
    }

    public ESensorRecord() {

    }

    public int getSensorID() { return this.sensorID; }
    public double getValue() { return this.value; }
    public int getTimestamp() { return this.timestamp; }

    @Override
    public String toString() {
        return String.format("%d - Sensor %d: %f", this.timestamp, this.sensorID, this.value);
    }

}
