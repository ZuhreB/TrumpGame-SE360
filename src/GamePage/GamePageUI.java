package src.GamePage;

import src.Connection;
import src.GameLogic;
import src.Model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class GamePageUI extends JFrame {

    private JLabel myScoreLabel;
    private JLabel opponentScoreLabel;
    JLabel playFlowLabel= new JLabel();
    JLabel trumpLabel= new JLabel();
    private final Color BG_COLOR = new Color(40, 44, 52);
    private final Color PANEL_COLOR = new Color(60, 63, 65);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color BUTTON_COLOR = new Color(180, 60, 60);
    private final Color CARD_COLOR = new Color(200, 200, 200);
    private final Color HIGHLIGHT_COLOR = new Color(255, 215, 0);
    private JPanel selectedCardPanel = null; // Hangi kartın seçili olduğunu tutar
    private JPanel opponentSelectedCardPanel = null; // Rakibin seçtiği kartı tutar
    private JPanel opponnentPlayedHandCardPanel = new JPanel();



    Card cardClosed=new Card("src/cards/card_close.jpg","1",true,"");


    private static GamePageUI instace = new GamePageUI();


    //SONRADAN MÜDAHELE EDİLECEK PANELLER
    JPanel bottomGrid;
    JPanel topGrid;
    JPanel westPanel;
    JPanel eastPanel;
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

        playFlowLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        playFlowLabel.setForeground(TEXT_COLOR);
        playFlowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        trumpLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        trumpLabel.setForeground(TEXT_COLOR);
        trumpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        opponnentPlayedHandCardPanel.setBackground(PANEL_COLOR);


        westPanel.add(infoTitle);
        westPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        westPanel.add(myScoreLabel);
        westPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        westPanel.add(opponentScoreLabel);
        westPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        westPanel.add(playFlowLabel);
        westPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        westPanel.add(trumpLabel);
        westPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        westPanel.add(opponnentPlayedHandCardPanel);
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
            topGrid.add(createCardPlaceholder(cardClosed,false,true));
        }

        bottomGrid = new JPanel(new GridLayout(2, 5, 10, 10));
        bottomGrid.setBackground(BG_COLOR);

        for (int i = 0; i < 10; i++) {
            bottomGrid.add(createCardPlaceholder(cardClosed,false,false));
        }

        centerPanel.add(topGrid);
        centerPanel.add(bottomGrid);

        add(centerPanel, BorderLayout.CENTER);

        eastPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        eastPanel.setBackground(PANEL_COLOR);
        eastPanel.setPreferredSize(new Dimension(150, 0));
        eastPanel.setBorder(new EmptyBorder(3, 10, 3, 10));

        for (int i = 0; i < 6; i++) {
            JPanel cards = createCardPlaceholder(cardClosed,true,false);
            cards.setBackground(new Color(100, 149, 237));
            eastPanel.add(cards);
        }

        add(eastPanel, BorderLayout.EAST);
        setVisible(true);
    }

    private JPanel createCardPlaceholder(Card card,boolean isRightCard, boolean isTopGrid) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_COLOR);
        //Kart paneliyle card objesi arasında bağıntı
        cardPanel.putClientProperty("card", card);
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

        if(!isTopGrid){
            cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.WAIT) return;
                    if (selectedCardPanel != null) {
                        selectedCardPanel.setBorder(null);
                    }
                    selectedCardPanel = cardPanel;
                    if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.PLAY ||
                            GameManager.getInstance().controlIfClickable(
                                    (Card) selectedCardPanel.getClientProperty("card"),
                                    (Card) opponentSelectedCardPanel.getClientProperty("card")
                            )
                    )
                    {
                        cardPanel.setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 4));
                        GamePageLogic.getInstance().controlSendingCard(card);
                    }

                }
            });
        }

        cardPanel.add(label, BorderLayout.CENTER);
        return cardPanel;
    }

    public JPanel getOpponentSelectedCardPanel() {
        return opponentSelectedCardPanel;
    }

    public JPanel getSelectedCardPanel() {
        return selectedCardPanel;
    }

    public JPanel getOpponentLastPlayedHandCards(){
        return opponnentPlayedHandCardPanel;
    }

    public void refreshGrids() {
        bottomGrid.removeAll();
        JPanel emptyPanel=new JPanel();
        emptyPanel.setBackground(BG_COLOR);


        Role myRole = GameState.getInstance().getMe().getRole();
        ArrayList<Card> myBoardCards = GameState.getInstance().getMe().getBoard_cards();

        if (myBoardCards != null && myBoardCards.size() >= 20) {
            if (myRole == Role.GUEST) {
                for (int i = 19; i >= 10; i--) {
                    if(!myBoardCards.get(i).isTaken()){
                        bottomGrid.add(createCardPlaceholder(myBoardCards.get(i), false,false));
                    }else{
                        if(!myBoardCards.get(i-10).isTaken()){
                            bottomGrid.add(createCardPlaceholder(myBoardCards.get(i-10), false,false));
                        }else{
                            bottomGrid.add(emptyPanel);//bottom gridd add boş kart
                        }
                    }
                }
            } else {
                for (int i = 10; i < 20; i++) {
                    if(!myBoardCards.get(i).isTaken()){
                        bottomGrid.add(createCardPlaceholder(myBoardCards.get(i), false,false));
                    }else if(!myBoardCards.get(i-10).isTaken()){
                            bottomGrid.add(createCardPlaceholder(myBoardCards.get(i-10), false,false));
                    }else{
                       bottomGrid.add(emptyPanel);//bottom gridd add boş kart
                    }


                }
            }
        }

        bottomGrid.revalidate();
        bottomGrid.repaint();

        // ---------------- TOP GRID (RAKİBİN KARTLARI) ----------------
        topGrid.removeAll();
        ArrayList<Card> myOpponentBoardCards = GameState.getInstance().getOpponent().getBoard_cards();

        if (myOpponentBoardCards != null && myOpponentBoardCards.size() >= 20) {
            if(myRole==Role.GUEST){
                for (int i = 19; i > 9; i--) {
                    if(!myOpponentBoardCards.get(i).isTaken()){
                        topGrid.add(createCardPlaceholder(myOpponentBoardCards.get(i), false,true));
                    }else{
                        if(!myOpponentBoardCards.get(i-10).isTaken()){
                            topGrid.add(createCardPlaceholder(myOpponentBoardCards.get(i-10), false,true));
                        }else{
                            topGrid.add(emptyPanel);//bottom gridd add boş kart
                        }
                    }
                }
            }else{
                // Rakip kartları her zaman düz (veya isteğe göre ters) basılabilir.
                // Genelde rakip kartları standart (10-19) bırakılır.
                for (int i = 10; i < 20; i++) {
                    if(!myOpponentBoardCards.get(i).isTaken()){
                        topGrid.add(createCardPlaceholder(myOpponentBoardCards.get(i), false,true));
                    }else if(!myOpponentBoardCards.get(i-10).isTaken()){
                        topGrid.add(createCardPlaceholder(myOpponentBoardCards.get(i-10), false,true));
                    }else{
                        topGrid.add(emptyPanel);//bottom gridd add boş kart
                        //bottom gridd add boş kart
                    }
                }
            }

        }

        topGrid.revalidate();
        topGrid.repaint();
    }

    public void refreshWest(){
        if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.PLAY){
            playFlowLabel.setText("Oynama sırası sende");
        }else if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.PLAY_BACK){
            playFlowLabel.setText("Karşı oynama sırası sende");
        }else if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.WAIT){
            playFlowLabel.setText("Bekleniyor...");
        }
        opponnentPlayedHandCardPanel.removeAll();

        westPanel.revalidate();
        westPanel.repaint();
    }

    public void refreshRight(){
        if(!GameState.getInstance().getMe().isAbleToSeeHandCards()) return;
        eastPanel.removeAll();
        for(Card card:GameState.getInstance().getMe().getHand_cards()){
            card.setClose(false);
            if(!card.isTaken()){
                eastPanel.add(createCardPlaceholder(card,true,false));
            }
        }
        eastPanel.revalidate();
        eastPanel.repaint();
    }

    public void refreshScores() {
        int myScore = GameState.getInstance().getMe().getTaken_cards().size();
        int opponentScore = GameState.getInstance().getOpponent().getTaken_cards().size();

        myScoreLabel.setText("Sen: " + myScore);
        opponentScoreLabel.setText("Rakip: " + opponentScore);

        westPanel.revalidate();
        westPanel.repaint();
    }

    public void assignTrumpLabel(){
        if(GameState.getInstance().getSecilen_trump()!=null){
            trumpLabel.setText("Kozun: "+GameState.getInstance().getSecilen_trump());
        }
        trumpLabel.revalidate();
        trumpLabel.repaint();
    }

    // Rakibin oynadığı kartı topGrid'de bulup etrafına ışık (çerçeve) ekler
    public void highlightOpponentCard(Card card) {
        if (opponentSelectedCardPanel != null) {
            opponentSelectedCardPanel.setBorder(null);
        }
        // TopGrid içindeki tüm panelleri gez ve bulduğun kartı yak
        for (Component component : topGrid.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Card c = (Card) panel.getClientProperty("card");
                if (c != null && c.getNumber().equals(card.getNumber()) && c.getType().equals(card.getType())) {
                    opponentSelectedCardPanel = panel;
                    panel.setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 4));
                    return;
                }
            }
        }
        //TopGridde bulamazsa sol panelde göster
        opponnentPlayedHandCardPanel.removeAll();
        opponnentPlayedHandCardPanel.setBackground(PANEL_COLOR);
        card.setClose(false);
        // 2. Yeni kart panelini oluştur
        JPanel newCardPanel = createCardPlaceholder(card, false, false);

        // 3. Mevcut panelin İÇİNE ekle (Değişkeni değiştirmek yerine add yapıyoruz)
        opponnentPlayedHandCardPanel.add(newCardPanel);
        opponnentPlayedHandCardPanel=newCardPanel;
        opponentSelectedCardPanel.putClientProperty("card",card);
        // 4. Paneli yenile ki görünsün
        opponnentPlayedHandCardPanel.revalidate();
        opponnentPlayedHandCardPanel.repaint();

        //refreshWest();
    }

    public static GamePageUI getInstace() {
        return instace;
    }


    public void setOpponentSelectedCardPanel(JPanel opponentSelectedCardPanel) {
        this.opponentSelectedCardPanel = opponentSelectedCardPanel;
    }

    public void setOpponnentPlayedHandCardPanel(JPanel opponnentPlayedHandCardPanel) {
        this.opponnentPlayedHandCardPanel = opponnentPlayedHandCardPanel;
    }
}