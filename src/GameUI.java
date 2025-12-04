package src;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {

    private GameLogic logic;
    private JTextField nicknameField;
    private JButton hostButton;
    private JButton joinButton;
    private JDialog waitingDialog;

    private final Color BG_COLOR = new Color(40, 44, 52);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color BUTTON_COLOR = new Color(75, 110, 175);
    private final Font MAIN_FONT = new Font("Arial", Font.BOLD, 18);

    public GameUI(GameLogic logic) {
        super("Trump Game");
        this.logic = logic;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        JLabel titleLabel = new JLabel("TRUMP GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nickLabel = new JLabel("Kullanıcı Adı:");
        nickLabel.setFont(MAIN_FONT);
        nickLabel.setForeground(TEXT_COLOR);
        nickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nicknameField = new JTextField("Oyuncu");
        nicknameField.setFont(MAIN_FONT);
        nicknameField.setHorizontalAlignment(JTextField.CENTER);//içindeki textin alignmentı
        nicknameField.setMaximumSize(new Dimension(400, 40));
        nicknameField.setAlignmentX(Component.CENTER_ALIGNMENT);//kendisinin alignmentı

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        //horizontal aralık ve vertical aralığı ayarladık
        buttonsPanel.setOpaque(false);
        buttonsPanel.setMaximumSize(new Dimension(400, 120));

        hostButton = createStyledButton("Oda Kur (Host)");
        joinButton = createStyledButton("Oyuna Katıl");

        buttonsPanel.add(hostButton);
        buttonsPanel.add(joinButton);

        centerContainer.add(Box.createVerticalGlue());//boşluk veriyoruz
        centerContainer.add(titleLabel);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 50)));
        centerContainer.add(nickLabel);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        //kullanıcı adı labeli ve nicknamefield arası boşluk
        centerContainer.add(nicknameField);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 30)));
        //nicknamefield ve buttonspanel arası boşluk-flutterdaki sizedbox(30) gibi
        centerContainer.add(buttonsPanel);
        centerContainer.add(Box.createVerticalGlue());//boşluk veriyoruz

        mainPanel.add(centerContainer, BorderLayout.CENTER);

        hostButton.addActionListener(e -> {
            String nickname = nicknameField.getText();
            setTitle("Trump Game - " + nickname + " (Oda Sahibi)");
            logic.hostGame(nickname);
        });

        joinButton.addActionListener(e -> {
            String nickname = nicknameField.getText();
            setTitle("Trump Game - " + nickname + " (Misafir)");
            String code = JOptionPane.showInputDialog(this, "Oyun kodunu girin:");
            if (code != null) logic.joinGame(nickname, code);
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(MAIN_FONT);
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    public void showGuiMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showWaitingDialog(String code) {
        waitingDialog = new JDialog(this, "Bekleniyor...", true);
        waitingDialog.setSize(400, 200);
        waitingDialog.setLayout(new BorderLayout());

        JPanel msgPanel = new JPanel(new GridLayout(2, 1));
        JLabel codeLabel = new JLabel("Kod: " + code, SwingConstants.CENTER);
        codeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        msgPanel.add(codeLabel);
        msgPanel.add(new JLabel("Rakip bekleniyor...", SwingConstants.CENTER));

        waitingDialog.add(msgPanel, BorderLayout.CENTER);

        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(e -> {
            logic.cancelHost();
            waitingDialog.dispose();
        });
        waitingDialog.add(cancelButton, BorderLayout.SOUTH);

        waitingDialog.setLocationRelativeTo(this);
        SwingUtilities.invokeLater(() -> waitingDialog.setVisible(true));
    }

    public void closeWaitingDialog() {
        if (waitingDialog != null) {
            waitingDialog.dispose();
        }
    }
}