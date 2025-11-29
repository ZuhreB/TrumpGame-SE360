package src;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {

    private JTextArea messageArea;
    private JTextField textField;
    private JButton sendButton;
    private GameLogic logic; // UI'ın komut göndereceği ana mantık sınıfı

    public GameUI(GameLogic logic) {
        super("Trump Game");
        this.logic = logic;

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sendButton = new JButton("Gonder");

        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.trim().isEmpty()) {
            logic.handleUserInput(message);
            textField.setText("");
        }
    }

    public void showGuiMessage(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }
}
