package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GamePageUI extends JFrame {

    private GameLogic logic;
    private JLabel myScoreLabel;
    private JLabel opponentScoreLabel;

    private final Color BG_COLOR = new Color(40, 44, 52);
    private final Color PANEL_COLOR = new Color(60, 63, 65);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color BUTTON_COLOR = new Color(180, 60, 60);
    private final Color CARD_COLOR = new Color(200, 200, 200);

    public GamePageUI(GameLogic logic, String nickname, boolean isHost) {
        super("Trump Game - " + nickname);
        this.logic = logic;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(PANEL_COLOR);
        northPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        northPanel.setPreferredSize(new Dimension(0, 60));

        JButton exitButton = new JButton("Çıkış");
        exitButton.setBackground(BUTTON_COLOR);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> {
            logic.disconnect();
            dispose();
        });

        JLabel titleLabel = new JLabel("TRUMP GAME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.ORANGE);

        northPanel.add(exitButton, BorderLayout.WEST);
        northPanel.add(titleLabel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBackground(PANEL_COLOR);
        westPanel.setPreferredSize(new Dimension(200, 0));
        westPanel.setBorder(new EmptyBorder(20, 20, 20, 20));//kenarlara uzaklık ekliyor


        JLabel infoTitle = new JLabel("SKOR TABLOSU");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 18));
        infoTitle.setForeground(Color.WHITE);
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        myScoreLabel = new JLabel("Sen");
        myScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        myScoreLabel.setForeground(TEXT_COLOR);
        myScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        opponentScoreLabel = new JLabel("Rakip");
        opponentScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        opponentScoreLabel.setForeground(TEXT_COLOR);
        opponentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        westPanel.add(infoTitle);
        westPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        westPanel.add(myScoreLabel);
        westPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        westPanel.add(opponentScoreLabel);
        westPanel.add(Box.createVerticalGlue());

        add(westPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        //vertical gap iki gridlayout arasındaki boşluk
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        //center panel ve new Gridlayout arasındaki boşluk

        JPanel topGrid = new JPanel(new GridLayout(2, 5, 10, 10));
        //burda da kartlar arası boşluklar ayarlanıyor
        topGrid.setBackground(BG_COLOR);


        for (int i = 0; i < 10; i++) {
            topGrid.add(createCardPlaceholder("Kart " + (i + 1)));
        }

        JPanel bottomGrid = new JPanel(new GridLayout(2, 5, 10, 10));
        bottomGrid.setBackground(BG_COLOR);

        for (int i = 0; i < 10; i++) {
            bottomGrid.add(createCardPlaceholder("Kart " + (i + 1)));
        }

        centerPanel.add(topGrid);
        centerPanel.add(bottomGrid);

        add(centerPanel, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        eastPanel.setBackground(PANEL_COLOR);
        eastPanel.setPreferredSize(new Dimension(150, 0));
        eastPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        for (int i = 0; i < 6; i++) {
            JPanel card = createCardPlaceholder("Yan " + (i + 1));
            card.setBackground(new Color(100, 149, 237));
            eastPanel.add(card);
        }

        add(eastPanel, BorderLayout.EAST);

        setVisible(true);
    }

    private JPanel createCardPlaceholder(String text) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(new LineBorder(Color.BLACK, 2));

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);

        card.add(label, BorderLayout.CENTER);
        return card;
    }
    
}