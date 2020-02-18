import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Server extends JFrame implements ActionListener {
    JTextField portField;
    JTextArea printArea;
    JButton toggleServer;
    ServerSocket ss;
    boolean isServerRunning;
    DateTimeFormatter dtf;

    public Server() {
        super();
        JPanel topBar = new JPanel();

        JLabel label = new JLabel("port (0-65535):");
        portField = new JTextField();
        portField.setColumns(5);
        topBar.add(label);
        topBar.add(portField);

        toggleServer = new JButton("Start server");
        topBar.add(toggleServer, BorderLayout.PAGE_END);
        getContentPane().add(topBar, BorderLayout.PAGE_START);

        printArea = new JTextArea();
        printArea.setRows(20);
        printArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(printArea);
        getContentPane().add(scrollPane,BorderLayout.CENTER);

        dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        toggleServer.addActionListener(this);
        isServerRunning = false;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingWorker<Boolean, Integer> sw = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                int port;
                if (!isServerRunning) {
                    try {
                        port = Integer.parseInt(portField.getText());
                    } catch (NumberFormatException nfe) {
                        port = 10000;
                        printArea.append(dtf.format(LocalTime.now()) + " - Illegal characters in port, trying port 10000.\n");
                    }
                    try {
                        ss = new ServerSocket(port);
                        printArea.append(dtf.format(LocalTime.now()) + " - Server started and listening on port: " + port + "\n");
                        isServerRunning = true;
                        toggleServer.setText("Close server");
                    } catch (java.io.IOException ioe) {
                        System.err.println(ioe.getMessage());
                        return false;
                    }
                    while (isServerRunning) {
                        try {
                            Socket s = ss.accept();
                            printArea.append(dtf.format(LocalTime.now()) + " - New connection by " + s.getInetAddress().getHostAddress() + "\n");
                            new ClientHandler(s,printArea).start();
                        } catch (java.io.IOException ioe) {

                        }
                    }
                } else {
                    isServerRunning = false;
                    try {
                        ss.close();
                        printArea.append(dtf.format(LocalTime.now()) + " - Server closed.\n");
                    } catch (IOException ioe) {
                        printArea.append(dtf.format(LocalTime.now()) + " - Server could not be closed.\n");
                    }
                }
                return true;
            }
        };
        sw.execute();
    }
}