package src.GamePage;

import src.Connection;
import src.Model.Card;
import src.Model.GameState;

import java.util.ArrayList;

public class GamePageLogic {
    private Connection conn = Connection.getInstance();
    private GameState gameState = GameState.getInstance();
    private static GamePageLogic instance = new GamePageLogic();

    private GamePageLogic(){};

    public static GamePageLogic getInstance() {
        return instance;
    }

    public void init(){
        if(gameState.getMe().getRole()=="Host"){
            Card.initAllCards();
            Card.shuffleAllCards();
            giveCardsToTheUser();
            conn.sendUser(gameState.getMe());
            conn.sendUser(gameState.getOpponent());
        }
    }

    private void giveCardsToTheUser(){
        ArrayList<Card> allCards=Card.getAllCards();
        ArrayList<Card> myBoardCards=new ArrayList<>();
        ArrayList<Card> opponentBoardCards=new ArrayList<>();
        ArrayList<Card> myHandCards=new ArrayList<>();
        ArrayList<Card> opponentHandCards=new ArrayList<>();


        for(int i=0;i<20;i++){
            myBoardCards.add(allCards.remove(i));
        }

        for(int i=0;i<20;i++){
            opponentBoardCards.add(allCards.remove(i));
        }

        for(int i=0;i<6;i++){
            myHandCards.add(allCards.remove(i));
        }

        for(int i=0;i<6;i++){
            opponentBoardCards.add(allCards.remove(i));
        }

        gameState.getMe().setBoard_cards(myBoardCards);
        gameState.getMe().setHand_cards(myHandCards);
        gameState.getOpponent().setBoard_cards(opponentBoardCards);
        gameState.getOpponent().setHand_cards(opponentHandCards);


    }




}
