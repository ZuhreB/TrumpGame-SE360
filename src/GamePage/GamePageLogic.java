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

    public void init(){
        if(GameState.getInstance().getMe().getRole()== Role.HOST){
            Card.initAllCards();
            Card.shuffleAllCards();
            giveCardsToTheUser();
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
            opponentBoardCards.add(allCards.get(i));
        }

        GameState.getInstance().getMe().setBoard_cards(myBoardCards);
        GameState.getInstance().getMe().setHand_cards(myHandCards);
        GameState.getInstance().getOpponent().setBoard_cards(opponentBoardCards);
        GameState.getInstance().getOpponent().setHand_cards(opponentHandCards);


    }


}
