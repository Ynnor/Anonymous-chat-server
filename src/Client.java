import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Client extends JFrame implements ActionListener {
    private JTextField addressField, portField, messageField;
    private JButton connectButton, disconnectButton, sendButton;
    private JTextArea printArea;
    private DateTimeFormatter dtf;
    private Socket s;
    private BufferedWriter os;
    private BufferedReader is;

    public Client() {
        super();

        JPanel topBar = new JPanel();
        JLabel label = new JLabel("Address:");
        addressField = new JTextField("localhost");
        addressField.setColumns(15);
        topBar.add(label);
        topBar.add(addressField);

        label = new JLabel("port (0-65535):");
        portField = new JTextField("10000");
        portField.setColumns(5);
        topBar.add(label);
        topBar.add(portField);

        connectButton = new JButton("Connect");
        connectButton.setActionCommand("connect");
        connectButton.addActionListener(this);
        topBar.add(connectButton);

        disconnectButton = new JButton("Disonnect");
        disconnectButton.setEnabled(false);
        disconnectButton.setActionCommand("disconnect");
        disconnectButton.addActionListener(this);
        topBar.add(disconnectButton);
        getContentPane().add(topBar, BorderLayout.PAGE_START);

        printArea = new JTextArea(15,30);
        printArea.setEditable(false);
        printArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(printArea);
        getContentPane().add(scrollPane,BorderLayout.CENTER);

        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new BoxLayout(bottomBar, BoxLayout.LINE_AXIS));
        messageField = new JTextField("");
        bottomBar.add(messageField, BorderLayout.PAGE_START);

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.setActionCommand("send");
        sendButton.addActionListener(this);
        bottomBar.add(sendButton);
        getContentPane().add(bottomBar, BorderLayout.PAGE_END);

        dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "connect":
                connect();
                break;
            case "disconnect":
                disconnect();
                break;
            case "send":
                send();
                break;
        }
    }

    public void connect() {
        try {
            int port = Integer.parseInt(portField.getText());
            if (port >= 0 && port < 65536) {
                try {
                    s = new Socket(addressField.getText(), port);
                    s.setSoTimeout(10000);
                    os = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    is = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    printArea.append(dtf.format(LocalTime.now()) + " - Connected to server " + addressField.getText() + " on " + port + ".\n");
                    disconnectButton.setEnabled(true);
                    sendButton.setEnabled(true);
                    connectButton.setEnabled(false);
                    SwingWorker<Boolean, Integer> sw = new SwingWorker<>() {
                        @Override
                        protected Boolean doInBackground() {
                            while (s.isConnected()) {
                                String message;
                                try {
                                    message = is.readLine();
                                    printArea.append(dtf.format(LocalTime.now()) + " - Message from server: " + message + "\n");
                                } catch (IOException ioe) {
                                }
                            }
                            return true;
                        }
                    };
                    sw.execute();
                } catch (IOException ioe) {
                    printArea.append(dtf.format(LocalTime.now()) + " - Error when establishing connection to server.\n");
                    disconnect();
                }
            } else {
                printArea.append(dtf.format(LocalDateTime.now()) + " - Port number " + port + " is out of range. Port has to be between 0 and 655235\n");
            }
        } catch (NumberFormatException e) {
            printArea.append(dtf.format(LocalDateTime.now()) + " - Port number " + portField.getText() + " contains illegal characters.\n");
            System.err.println(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            os.close();
            is.close();
            s.close();
            os = null;
            is = null;
            s = null;
            printArea.append(dtf.format(LocalTime.now()) + " - Connection to server was closed.\n");
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendButton.setEnabled(false);
        } catch (IOException ioe) {
            printArea.append(dtf.format(LocalTime.now()) + " - Connection to server could not be closed.\n");
        }
    }

    public void send() {
        if (messageField.getText().length() < 1) {
            printArea.append(dtf.format(LocalTime.now()) + " - Enter a text message to randomize below.\n");
        } else {
            try {
                os.write(messageField.getText());
                os.newLine();
                os.flush();
                printArea.append(dtf.format(LocalTime.now()) + " - Sending message to server.\n");
            } catch (IOException ioe) {
                printArea.append(dtf.format(LocalTime.now()) + " - Error while sending message to server.\n");
            }
        }
    }
}