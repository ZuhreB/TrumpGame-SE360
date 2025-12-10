package src.GamePage;

import src.Connection;
import src.Model.Card;
import src.Model.GameState;
import src.Model.Role;

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

            Connection.getInstance().sendUserObject(GameState.getInstance().getMe());
            Connection.getInstance().sendUserObject(GameState.getInstance().getOpponent());
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
                    askForTrump();
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
        String [] options ={"Maça","Kupa","Sinek","Karo"};
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
        Connection.getInstance().sendMessage("Koz:" + selectedTrump);
        GameState.getInstance().getInstance().changeVisibility();
        GamePageUI.getInstace().refreshGrids();
    }

}
