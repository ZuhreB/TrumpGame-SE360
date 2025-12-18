package src.GamePage;

import src.Connection;
import src.Database.DatabaseManager;
import src.GameLogic;
import src.Model.*;

import javax.swing.*;
import java.util.ArrayList;

public class GamePageLogic {

    private static GamePageLogic instance = new GamePageLogic();
    private GamePageLogic(){};
    public static GamePageLogic getInstance() {
        return instance;
    }

   public void startHostGame(){
        if(GameState.getInstance().getMe().getRole() == Role.HOST){
            Card.initAllCards();
            Card.shuffleAllCards();
            giveCardsToTheUser();

            initTrumpMoment();

            Connection.getInstance().sendObject(GameState.getInstance().getMe());
            Connection.getInstance().sendObject(GameState.getInstance().getOpponent());
        }
    }

    private void giveCardsToTheUser(){
        ArrayList<Card> allCards=Card.getAllCards();
        ArrayList<Card> myBoardCards=new ArrayList<>();
        ArrayList<Card> opponentBoardCards=new ArrayList<>();
        ArrayList<Card> myHandCards=new ArrayList<>();
        ArrayList<Card> opponentHandCards=new ArrayList<>();
        System.out.println(allCards.size());

        for(int i=0;i<20;i++){
            myBoardCards.add(allCards.get(i));
        }

        for(int i=20;i<40;i++){
            opponentBoardCards.add(allCards.get(i));
        }

        for(int i=40;i<46;i++){
            myHandCards.add(allCards.get(i));
        }

        for(int i=46;i<52;i++){
            opponentHandCards.add(allCards.get(i));
        }

        GameState.getInstance().getMe().setBoard_cards(myBoardCards);
        GameState.getInstance().getMe().setHand_cards(myHandCards);
        GameState.getInstance().getOpponent().setBoard_cards(opponentBoardCards);
        GameState.getInstance().getOpponent().setHand_cards(opponentHandCards);
    }

    public void initTrumpMoment() {
        if (GameState.getInstance().getMe()!=null&&GameState.getInstance().getMe().getRole() == Role.GUEST) {
            ArrayList<Card> myBoardCards = GameState.getInstance().getMe().getBoard_cards();
            if (myBoardCards != null && myBoardCards.size() >= 20) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    GamePageUI.getInstace().refreshGrids();
                    askForTrump();

                });
            }
            /// FONKSİYON BURAYA GELECEK.
        } else if (GameState.getInstance().getOpponent().getRole()==Role.GUEST) {
            ArrayList<Card> opponentBoardCards = GameState.getInstance().getOpponent().getBoard_cards();
            if (opponentBoardCards != null && opponentBoardCards.size() >= 20) {
                for (int i = 15; i < 20; i++) {
                    opponentBoardCards.get(i).setClose(false);
                }
                javax.swing.SwingUtilities.invokeLater(() -> {
                    GamePageUI.getInstace().refreshGrids();
                });
            }
        }
        for(Card card: GameState.getInstance().getMe().getBoard_cards()){
            System.out.print(card.isClose());
        }
        System.out.println("-------------------");
        for(Card card: GameState.getInstance().getOpponent().getBoard_cards()){
            System.out.print(card.isClose());
        }
    }

    private void askForTrump(){
        String [] options ={"spades","hearts","clubs","diamonds"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Kozu seçiniz",
                "Koz Belirleme",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        String selectedTrump = "";
        switch (choice) {
            case 0: selectedTrump = "spades"; break;
            case 1: selectedTrump = "hearts"; break;
            case 2: selectedTrump = "clubs"; break;
            case 3: selectedTrump = "diamonds"; break;
            default: selectedTrump = "spades"; // bu varsayılan olcak hiç bişi seçmeden kapatırsa diye
        }
        System.out.println("Koz:"+selectedTrump);
        GameState.getInstance().setSecilen_trump(selectedTrump);

        // --- GUEST İÇİN DATABASE KAYDI ---
        GameState.getInstance().setHostId(DatabaseManager.getInstance().getOrCreateUser(GameState.getInstance().getOpponent().getNickName()+"host"));
        GameState.getInstance().setGuestId(DatabaseManager.getInstance().getOrCreateUser(GameState.getInstance().getMe().getNickName()+"guest"));
        GameState.getInstance().setDbGameId(DatabaseManager.getInstance().createGame(GameState.getInstance().getHostId(), GameState.getInstance().getGuestId(), selectedTrump));


        Connection.getInstance().makeMapAndSend(MessageType.TRUMP,selectedTrump);
        GameState.getInstance().setPlayFlow(PLAY_FLOW.PLAY);
        GameState.getInstance().getInstance().makeAllCardsVisible();
        GamePageUI.getInstace().refreshGrids();
        GamePageUI.getInstace().assignTrumpLabel();
    }

    public void controlSendingCard(Card card){
        if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.WAIT){
            GameLogic.getInstance().showGameMessage("YOU CAN NOT PLAY NOW WAIT");
        }else if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.PLAY){
            // oynadığım hamleyi kaydediyorum
            int gameId = GameState.getInstance().getDbGameId();
            int myId = (GameState.getInstance().getMe().getRole() == Role.HOST) ?
                    GameState.getInstance().getHostId() : GameState.getInstance().getGuestId();if(gameId != -1) DatabaseManager.getInstance().saveMove(gameId, myId, card, GameState.getInstance().getTurnStep(), PLAY_FLOW.PLAY);

            Connection.getInstance().makeMapAndSend(MessageType.PLAYED,card);
            GameState.getInstance().setPlayFlow(PLAY_FLOW.WAIT);
        }else if (GameState.getInstance().getPlayFlow()==PLAY_FLOW.PLAY_BACK){
            // oynadığım hamleyi kaydediyorum
            int gameId = GameState.getInstance().getDbGameId();
            int myId = (GameState.getInstance().getMe().getRole() == Role.HOST) ?
                    GameState.getInstance().getHostId() : GameState.getInstance().getGuestId();if(gameId != -1) DatabaseManager.getInstance().saveMove(gameId, myId, card, GameState.getInstance().getTurnStep(), PLAY_FLOW.PLAY_BACK);

            Connection.getInstance().makeMapAndSend(MessageType.PLAYED_BACK,card);
            GameState.getInstance().setPlayFlow(PLAY_FLOW.WAIT);
            GameManager.getInstance().decideWhoTake(card,getOpponentLastPlayedCard(),PLAY_FLOW.PLAY_BACK);
        }
    }

    public Card findLocalCard(Card networkCard) {
        if (GameState.getInstance().getOpponent() != null && GameState.getInstance().getOpponent().getBoard_cards() != null) {
            for (Card c : GameState.getInstance().getOpponent().getBoard_cards()) {
                if (c.getNumber().equals(networkCard.getNumber()) && c.getType().equals(networkCard.getType())) {
                    return c; // Eşleşen yerel kartı döndür ama board carddan
                }
            }

            for(Card c : GameState.getInstance().getOpponent().getHand_cards()){
                if (c.getNumber().equals(networkCard.getNumber()) && c.getType().equals(networkCard.getType())) {
                    return c; // Eşleşen yerel kartı döndür ama hand carddan
                }
            }
        }
        return networkCard; // Bulunamazsa (hata durumu) gelen kartı geri döndür
    }

    public Card getOpponentLastPlayedCard(){
        Card card= (Card) GamePageUI.getInstace().getOpponentSelectedCardPanel().getClientProperty("card");
        if(card==null){
            //Bu noktada ben play back modundayım, ve rakibimin son oynadığı kart board cardstan değil handden
            card = (Card) GamePageUI.getInstace().getOpponentLastPlayedHandCards().getClientProperty("card");
        }
        return card;
    }

    public Card getMyLastPlayedCard(){
        return (Card) GamePageUI.getInstace().getSelectedCardPanel().getClientProperty("card");
    }

}
