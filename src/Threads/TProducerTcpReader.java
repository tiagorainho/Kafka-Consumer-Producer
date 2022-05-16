package Threads;

import java.io.IOException;
import java.net.ServerSocket;

import Communications.TServerConsumer;
import Entities.ESensorRecord;
import Interfaces.IConsumer;
import Monitors.MFifo;

public class TProducerTcpReader extends Thread {
    
    private final int portNumber;
    private final MFifo<String> fifo;

    public TProducerTcpReader(int portNumber, MFifo<String> fifo) {
        this.portNumber = portNumber;
        this.fifo = fifo;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) { 
            while (true) {
                TServerConsumer consumer = new TServerConsumer(serverSocket.accept(), this.fifo);
                consumer.start();
            }
        } catch (IOException e) {
            System.out.println("Error on server sockets");
            System.exit(-1);
        }
    }
}
