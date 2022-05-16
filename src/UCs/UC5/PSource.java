package UCs.UC5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import Communications.TClientSource;
import Entities.ESource;
import Monitors.MFifo;


public class PSource {

    public static void main(String args[]) {
        
        // prepare Source object
        File file = new File("dataset/sensor.txt");
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ESource source = new ESource(fileReader);
        MFifo<String> fifo = new MFifo<String>(1000);

        // thread communication
        Socket socket;
        try {
            socket = new Socket("localhost", 999);
            TClientSource communications = new TClientSource(socket, fifo);
            communications.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // populate the fifo
        source.fillFifo(fifo);
    }
}
