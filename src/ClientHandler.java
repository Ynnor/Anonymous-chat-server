import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread {

    private static List<ClientHandler> connectedClients = new ArrayList<>();
    private Socket s;
    private BufferedWriter os;
    private BufferedReader is;
    private StringWriter sw;
    private WordRandomizerFilterWriter wr;
    private JTextArea printArea;
    DateTimeFormatter dtf;

    ClientHandler(Socket s, JTextArea printArea) {
        this.s = s;
        this.printArea = printArea;
        sw = new StringWriter();
        wr = new WordRandomizerFilterWriter(new PrintWriter(sw));
        connectedClients.add(this);
        dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        try {
            os = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (java.io.IOException e) {
            printArea.append(dtf.format(LocalTime.now()) + " - Datastreams could not be established for client " + s.getInetAddress().getHostAddress() + "\n");
            close();
        }
    }

    @Override
    public void run() {
        int length;
        String message;
        byte[] data;
        while (s.isConnected()) {
            try {
                message = is.readLine();
                if(message == null) {
                    close();
                } else {
                    printArea.append(dtf.format(LocalTime.now()) + " - Received a message from " + s.getInetAddress().getHostAddress() + "\n");
                    printArea.append(dtf.format(LocalTime.now()) + " - Randomizing and sending an answer...\n");
                    wr.write(message);
                    wr.flush();
                    String answer = sw.toString();
                    sw.getBuffer().setLength(0);
                    for (ClientHandler client : connectedClients) {
                        client.send(answer);
                    }
                }

            } catch (java.io.IOException e) {
            }

        }
        close();
    }

    public void close() {
        try {
            connectedClients.remove(this);
            is.close();
            os.close();
            s.close();
            printArea.append(dtf.format(LocalTime.now()) + " - Connection to " + s.getInetAddress().getHostAddress() + " closed.\n");
            interrupt();
        } catch (java.io.IOException e) {
            printArea.append(dtf.format(LocalTime.now()) + " - Connection to " + s.getInetAddress().getHostAddress() + " could not be closed.\n");
        }
    }

    public void send(String message) {
        try {
            os.write(message);
            os.newLine();
            os.flush();
        } catch (IOException ioe) {
            printArea.append(dtf.format(LocalTime.now()) + " - Unable to send message to client at " + s.getInetAddress().getHostAddress() + ".\n");
        }
    }
}