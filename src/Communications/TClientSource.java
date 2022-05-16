package Communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import Monitors.MFifo;

public class TClientSource extends Thread {
    

    private String hostName = "localhost";
    private int portNumber = 999;
    private Socket socket;
    private PrintWriter out;
    private MFifo<String> fifo;
    private boolean endOfStream;
    

    public TClientSource(Socket socket, MFifo<String> fifo){
        this.fifo = fifo;
        this.socket = socket;
        this.endOfStream = false;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException e) {

        } catch (IOException e) {

        }
        System.out.println("Comunications has started on " + hostName + ":" + portNumber);
    }

    public boolean endOfStream() {
        return this.endOfStream;
    }

    @Override
    public void run() {
        String node;
        while (!(fifo.isBlocked() && fifo.isEmpty())) {
            node = fifo.pop();
            out.println(node);
            out.flush();
        }
        node = null;
        out.println(node);
        out.flush();
    }

}
