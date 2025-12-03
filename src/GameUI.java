package src;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {

    private GameLogic logic;
    private JTextField nicknameField;
    private JButton hostButton;
    private JButton joinButton;
    private JDialog waitingDialog;

    public GameUI(GameLogic logic) {
        super("Trump Game");
        this.logic = logic;

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        
        JLabel titleLabel = new JLabel("TRUMP GAME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setBounds(0, 20, 500, 50);
        add(titleLabel);

        nicknameField = new JTextField("Oyuncu");
        nicknameField.setBounds(150, 150, 200, 30);
        add(nicknameField);

        hostButton = new JButton("Oyunu Kur (Oda sahibi)");
        hostButton.setBounds(100, 250, 300, 40);
        add(hostButton);

        joinButton = new JButton("Oyuna Katil");
        joinButton.setBounds(100, 300, 300, 40);
        add(joinButton);

        hostButton.addActionListener(e -> {
            String nickname = nicknameField.getText();
            setTitle("Trump Game - " + nickname + " (Oda sahibi)");
            logic.hostGame(nickname);
        });

        joinButton.addActionListener(e -> {
            String nickname = nicknameField.getText();
            setTitle("Trump Game - " + nickname + " (Misafir)");
            String hostIp = JOptionPane.showInputDialog(this, "Oyunun bağlantı adresini girin:");
            logic.joinGame(nickname,hostIp);
        });
    }
    
    public void showGuiMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showWaitingDialog(String ip) {
        waitingDialog = new JDialog(this, "Rakip Bekleniyor...", true);
        waitingDialog.setSize(300, 150);
        waitingDialog.setLayout(new FlowLayout());
        waitingDialog.setLocationRelativeTo(this);

        waitingDialog.add(new JLabel("Bağlantı adresi: " + ip));
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