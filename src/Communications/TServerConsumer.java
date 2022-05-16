package Communications;

import java.io.*;
import java.net.Socket;

import Entities.ESensorRecord;
import Interfaces.IConsumer;
import Monitors.MFifo;

public class TServerConsumer extends Thread {
    private Socket socket = null;
    private MFifo<String> fifo;
    private boolean endOfStream;

    /**
     * Constructor to instantiate a TMultiServer object
     * @param socket Client Server socket
     * @param control SimulationControl interface
     */
    public TServerConsumer(Socket socket, MFifo<String> fifo) {
        super("MultiServerThread");
        this.socket = socket;
        this.fifo = fifo;
        this.endOfStream = false;
    }
     
    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        ) {
            this.fifo.unblockPuts();
            String inputLine;
            while (!(inputLine = in.readLine()).equals("null")) {
                this.fifo.put(inputLine);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.fifo.blockPuts();
        }
    }

    public boolean endOfStream() {
        return this.endOfStream;
    }


}