package Entities;

import java.util.Scanner;

import Monitors.MFifo;

public class ESource {

    private Scanner scanner;

    public void fillFifo(MFifo<String> fifo) {
        String nextValue = "";
        int c = 0;
        while(this.scanner.hasNext()) {
            nextValue = "";
            for(int i = 0; i<3;i++) {
                nextValue += this.scanner.nextLine();
                if(i < 2)
                    nextValue += "-";
            }
            fifo.put(String.valueOf(nextValue));

            
            //if(c >= 1000 )
            //    break;
            System.out.println(c++);
        }
        fifo.blockPuts();
    }

    public ESource(Scanner scanner) {
        this.scanner = scanner;
    }

}