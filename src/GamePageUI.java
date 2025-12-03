package src;

import javax.swing.*;
import java.awt.*;

public class GamePageUI extends JFrame {

    private GameLogic logic;
    private JLabel statusLabel;

    public GamePageUI(GameLogic logic, String nickname, boolean isHost) {
        super("Trump Game - " + nickname + (isHost ? " (Oda Sahibi)" : " (Misafir)"));
        this.logic = logic;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        statusLabel = new JLabel("Bağlantı Kuruldu. Oyun Başlamak Üzere...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(statusLabel);

        add(mainPanel, BorderLayout.CENTER);

        JButton exitButton = new JButton("Oyundan Çık");
        exitButton.addActionListener(e -> {
            logic.disconnect();
            dispose();
        });
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(exitButton);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void displayStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
        });
    }

}