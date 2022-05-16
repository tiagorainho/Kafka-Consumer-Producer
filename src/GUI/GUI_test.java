package GUI;

import java.util.Random;

import Entities.ESensorRecord;
import GUI.entities.PProducerGUI;
import GUI.entities.PConsumerGUI;

public class GUI_test {
    
    public static void main(String args[]) {
        int numProducers=5;
        PProducerGUI p= new PProducerGUI();
        PConsumerGUI c= new PConsumerGUI(3,3,2);

        for(int i=0;i<500;i++){

            ESensorRecord record= new ESensorRecord(new Random().nextInt(5)+1, new Random().nextInt(15)+15 , i);
            p.addData(record,i%6);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
}
