import javax.swing.*;

public class ServerStarter {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Server().setVisible(true));
    }
}
