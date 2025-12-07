package src.GamePage;

import src.GameLogic;
import src.Model.Card;
import src.Model.GameState;
import src.Model.Role;
import src.Model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class GamePageUI extends JFrame {

    private JLabel myScoreLabel;
    private JLabel opponentScoreLabel;

    private final Color BG_COLOR = new Color(40, 44, 52);
    private final Color PANEL_COLOR = new Color(60, 63, 65);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color BUTTON_COLOR = new Color(180, 60, 60);
    private final Color CARD_COLOR = new Color(200, 200, 200);

    Card card=new Card("src/cards/card_close.jpg","Lu",true,"");
    private static GamePageUI instace = new GamePageUI();


    //SONRADAN MÜDAHELE EDİLECEK PANELLER
    JPanel bottomGrid;
    JPanel topGrid;
    JPanel westPanel;
    private GamePageUI() {
        super("Trump Game - ");
        System.out.println("game page ui constructoru başlatıldı");
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
            GameLogic.getInstance().disconnect();
            dispose();
        });

        JLabel titleLabel = new JLabel("TRUMP GAME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.ORANGE);

        northPanel.add(exitButton, BorderLayout.WEST);
        northPanel.add(titleLabel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        westPanel = new JPanel();
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

        topGrid = new JPanel(new GridLayout(2, 5, 10, 10));
        //burda da kartlar arası boşluklar ayarlanıyor
        topGrid.setBackground(BG_COLOR);


        for (int i = 0; i < 10; i++) {
            topGrid.add(createCardPlaceholder(card,false));
        }

        bottomGrid = new JPanel(new GridLayout(2, 5, 10, 10));
        bottomGrid.setBackground(BG_COLOR);

        for (int i = 0; i < 10; i++) {
            bottomGrid.add(createCardPlaceholder(card,false));
        }

        centerPanel.add(topGrid);
        centerPanel.add(bottomGrid);

        add(centerPanel, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        eastPanel.setBackground(PANEL_COLOR);
        eastPanel.setPreferredSize(new Dimension(150, 0));
        eastPanel.setBorder(new EmptyBorder(3, 10, 3, 10));

        for (int i = 0; i < 6; i++) {
            JPanel cards = createCardPlaceholder(card,true);
            cards.setBackground(new Color(100, 149, 237));
            eastPanel.add(cards);
        }

        add(eastPanel, BorderLayout.EAST);
        setVisible(true);
    }

    private JPanel createCardPlaceholder(Card card,boolean isRightCard) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setOpaque(false);

        // Card sınıfındaki static adresi kullanarak resmi yükle
        String cardImageAdress=card.isClose()?Card.CLOSED_FACE_ADDRESS:card.getPng_address();
        ImageIcon imageIcon = new ImageIcon(cardImageAdress);

        // Resmi karta sığacak şekilde boyutlandır
        Image image = imageIcon.getImage();
        if(isRightCard){
            Image newimg = image.getScaledInstance(70, 90,  java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newimg);
        }else{
            Image newimg = image.getScaledInstance(80, 120,  java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newimg);
        }


        // Label'ı artık text ile değil, icon ile oluşturuyoruz
        JLabel label = new JLabel(imageIcon);

        cardPanel.add(label, BorderLayout.CENTER);
        return cardPanel;
    }

    public void refreshGrids() {
        bottomGrid.removeAll();

        Role myRole = GameState.getInstance().getMe().getRole();
        ArrayList<Card> myBoardCards = GameState.getInstance().getMe().getBoard_cards();

        if (myBoardCards != null && myBoardCards.size() >= 20) {

            if (myRole == Role.GUEST) {
                for (int i = 19; i >= 10; i--) {
                    bottomGrid.add(createCardPlaceholder(myBoardCards.get(i), false));
                }
            } else {
                for (int i = 10; i < 20; i++) {
                    bottomGrid.add(createCardPlaceholder(myBoardCards.get(i), false));
                }
            }
        }

        bottomGrid.revalidate();
        bottomGrid.repaint();

        // ---------------- TOP GRID (RAKİBİN KARTLARI) ----------------
        topGrid.removeAll();
        ArrayList<Card> myOpponentBoardCards = GameState.getInstance().getOpponent().getBoard_cards();

        if (myOpponentBoardCards != null && myOpponentBoardCards.size() >= 20) {
            // Rakip kartları her zaman düz (veya isteğe göre ters) basılabilir.
            // Genelde rakip kartları standart (10-19) bırakılır.
            for (int i = 10; i < 20; i++) {
                topGrid.add(createCardPlaceholder(myOpponentBoardCards.get(i), false));
            }
        }

        topGrid.revalidate();
        topGrid.repaint();
    }

    public static GamePageUI getInstace() {
        return instace;
    }
}