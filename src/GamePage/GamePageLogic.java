package src.GamePage;

import src.Connection;
import src.Model.Card;
import src.Model.GameState;
import src.Model.Role;

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
        System.out.println("init trump momente girildi");
        for(Card card: GameState.getInstance().getMe().getBoard_cards()){
            System.out.print(card.isClose());
        }
        for(Card card: GameState.getInstance().getOpponent().getBoard_cards()){
            System.out.print(card.isClose());
        }
        if (GameState.getInstance().getMe().getRole() == Role.GUEST) {
            System.out.println("ben kartlarını açacak 5 adet");

            ArrayList<Card> myBoardCards = GameState.getInstance().getMe().getBoard_cards();
            if (myBoardCards != null && myBoardCards.size() >= 20) {
                for (int i = 10; i < 15; i++) {
                    System.out.println("kart açıldı");
                    myBoardCards.get(i).setClose(false);
                }
                javax.swing.SwingUtilities.invokeLater(() -> {
                    GamePageUI.getInstace().refreshGrids();
                });
            }
        } else if (GameState.getInstance().getOpponent().getRole()==Role.GUEST) {
            System.out.println("rakip kartlarını açacak 5 adet");
            ArrayList<Card> opponentBoardCards = GameState.getInstance().getOpponent().getBoard_cards();
            if (opponentBoardCards != null && opponentBoardCards.size() >= 20) {
                for (int i = 15; i < 20; i++) {
                    System.out.println("kart açıldı");
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
        for(Card card: GameState.getInstance().getOpponent().getBoard_cards()){
            System.out.print(card.isClose());
        }
    }

}
