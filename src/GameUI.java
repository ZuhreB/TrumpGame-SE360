package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameUI extends JFrame {

    private GameLogic logic;
    private JTextField nicknameField;
    private JButton hostButton;
    private JButton joinButton;
    private JDialog waitingDialog;

    private JTextField messageField;
    private JButton sendButton;

    public GameUI(GameLogic logic) {
        super("Trump Game");
        this.logic = logic;

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("TRUMP GAME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        nicknameField = new JTextField("Oyuncu");
        nicknameField.setMaximumSize(new Dimension(200, 30));
        JPanel nicknamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nicknamePanel.add(nicknameField);
        centerPanel.add(nicknamePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        hostButton = new JButton("Oyunu Kur (Oda sahibi)");
        joinButton = new JButton("Oyuna Katil");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(hostButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(joinButton);

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonContainer.add(buttonPanel);
        centerPanel.add(buttonContainer);

        add(centerPanel, BorderLayout.CENTER);

        hostButton.addActionListener(e -> {
            String nickname = nicknameField.getText();
            setTitle("Trump Game - " + nickname + " (Oda sahibi)");
            logic.hostGame(nickname);
        });

        joinButton.addActionListener(e -> {
            String nickname = nicknameField.getText();
            setTitle("Trump Game - " + nickname + " (Misafir)");
            String code = JOptionPane.showInputDialog(this, "Oyunun kodunu girin:");
            logic.joinGame(nickname,code);
        });



        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            logic.sendMessage(message);
            messageField.setText("");
        }
    }

    public void showGuiMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showWaitingDialog(String code) {
        waitingDialog = new JDialog(this, "Rakip Bekleniyor...", true);
        waitingDialog.setSize(300, 150);
        waitingDialog.setLayout(new FlowLayout());
        waitingDialog.setLocationRelativeTo(this);

        waitingDialog.add(new JLabel("Bağlantı kodu: " + code));
        waitingDialog.add(new JLabel("Rakip bekleniyor..."));

        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(e -> {
            logic.cancelHost();
            waitingDialog.dispose();
        });
        waitingDialog.add(cancelButton);

        SwingUtilities.invokeLater(() -> waitingDialog.setVisible(true));
    }

    public void closeWaitingDialog() {
        if (waitingDialog != null) {
            waitingDialog.dispose();
        }
    }
}