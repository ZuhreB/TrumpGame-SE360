package src.GamePage;

import src.Connection;
import src.Model.Card;
import src.Model.GameState;

import java.util.ArrayList;

public class GamePageLogic {
    static Connection conn = new Connection();

    public static void init(){

        if(GameState.getMe().getRole()=="Host"){
            Card.initAllCards();
            Card.shuffleAllCards();
            giveCardsToTheUser();
            conn.sendUser(GameState.getMe());
            conn.sendUser(GameState.getOpponent());
        }
    }

    private static void giveCardsToTheUser(){
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





        GameState.getMe().setBoard_cards(myBoardCards);
        GameState.getMe().setHand_cards(myHandCards);
        GameState.getOpponent().setBoard_cards(opponentBoardCards);
        GameState.getOpponent().setHand_cards(opponentHandCards);


    }




}
