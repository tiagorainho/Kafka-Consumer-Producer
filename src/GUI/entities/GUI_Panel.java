package GUI.entities;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Entities.ESensorRecord;

public class GUI_Panel extends JPanel{
    private JTable table;
    private JLabel lTotal = new JLabel();
    private JLabel[] lSensor = new JLabel[6];
    private double[] max = new double[6];
    private double[] min = new double[6];
    private double[] avg = new double[6];
    private int total;
    private int[] sensorCount = new int[6];
    private Object[][] data={};
    private String[] columnNames={"Time Stamp","Sensor ID","Temperature"};
    private ReentrantLock rl = new ReentrantLock();
    private final int temperature;


    public GUI_Panel(int temperature){
        this.temperature=temperature;
        this.setLayout(null);
        this.add(lTotal);
        lTotal.setText("Total Values received: "+total);
        lTotal.setBounds(305,10,200,30);
        for(int i=0;i<6;i++){
            if(temperature==0){
                lSensor[i]=new JLabel("Sensor "+(i+1)+" Values received: "+sensorCount[i]);
                lSensor[i].setBounds(30+250*(i%3),40+((int)i/3)*30,250,30);
            }else if(temperature==1){
                lSensor[i]=new JLabel("Sensor "+(i+1)+" Values received: "+sensorCount[i]+"   Max: "+max[i]+"ºC  Min:"+min[i]+"ºC");
                lSensor[i].setBounds(10+400*(i%2),40+((int)i/2)*20,400,30);
            }else{
                lSensor[i]=new JLabel("Sensor "+(i+1)+" Values received: "+sensorCount[i]+"   Average: "+avg[i]+"ºC");
                lSensor[i].setBounds(60+360*(i%2),40+((int)i/2)*20,360,30);
            }
            this.add(lSensor[i]);
        }
        table=new JTable();
        table.setEnabled(false);
        table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20,120,750,380);
        this.add(scrollPane);
        scrollPane.setViewportView(table);
        table.setFillsViewportHeight(true);
    }

    public void addRow(ESensorRecord record){
        DefaultTableModel model=(DefaultTableModel)table.getModel();
        Object[] data=new Object[]{record.getTimestamp(),record.getSensorID(),record.getValue()};
        model.insertRow(0,data);
        total++;
        sensorCount[record.getSensorID()-1]++;
        updateLabels(record.getSensorID()-1);
    }

    public void updateMinMax(List<Double> minMax, int sensorID){
        min[sensorID-1]=minMax.get(0);
        max[sensorID-1]=minMax.get(1);
        updateLabels(sensorID-1);
    }

    public void updateAverage(double newAvg, int sensorID){
        avg[sensorID-1]=newAvg;
        updateLabels(sensorID-1);
    }

    private void updateLabels(int sensorID){
        try{
            rl.lock();
            lTotal.setText("Total Values received: "+total);
            if(temperature==0){
                lSensor[sensorID].setText("Sensor "+(sensorID+1)+" Values received: "+sensorCount[sensorID]);
            }else if(temperature==1) {
                lSensor[sensorID].setText("Sensor "+(sensorID+1)+" Values received: "+sensorCount[sensorID]+"   Max: "+max[sensorID]+"ºC  Min:"+min[sensorID]+"ºC");
            }else{
                lSensor[sensorID].setText("Sensor "+(sensorID+1)+" Values received: "+sensorCount[sensorID]+"   Average: "+ Math.round(avg[sensorID]*100.0)/100.0+"ºC");
            }
            
        }finally{
            rl.unlock();
        }
    }
}
