package src.GamePage;

import src.Connection;
import src.Model.Card;
import src.Model.GameState;
import src.Model.PLAY_FLOW;
import src.Model.User;

import java.util.List;

public class GameManager {
    private static GameManager instance = new GameManager();
    User winner=new User();
    private GameManager() {
    }

    ;

    public static GameManager getInstance() {
        return instance;
    }

    public void decideWhoTake(Card myCard, Card opponentCard,PLAY_FLOW myRole) {
        if (myCard.getType().equals(opponentCard.getType())) {
            winner = myCard.getNumberPower() > opponentCard.getNumberPower() ?
                    GameState.getInstance().getMe() :
                    GameState.getInstance().getOpponent();
        } else if(myCard.getType()==GameState.getInstance().getSecilen_trump() ||
                opponentCard.getType()==GameState.getInstance().getSecilen_trump()){
            winner=opponentCard.getType()==GameState.getInstance().getSecilen_trump()?
                    GameState.getInstance().getOpponent():
           GameState.getInstance().getMe();
        }else{
            if(myRole==PLAY_FLOW.PLAY){
                winner=GameState.getInstance().getMe();
            }else{
                winner=GameState.getInstance().getOpponent();
            }
        }
        afterDecideWhoTakeCard(myCard, opponentCard);


        GamePageUI.getInstace().refreshGrids();
        GamePageUI.getInstace().refreshWest();
    }

    void afterDecideWhoTakeCard(Card myCard,Card opponentCard){
        myCard.setOwner(winner);
        opponentCard.setOwner(winner);
        myCard.setTaken(true);
        opponentCard.setTaken(true);

        winner.getTaken_cards().addAll(List.of(myCard, opponentCard));
        if(!winner.isAbleToSeeHandCards()&&winner==GameState.getInstance().getMe()) winner.setAbleToSeeHandCards(true);

        if(GameState.getInstance().getMe().equals(winner)){
            System.out.println("GAZANDIM");
            GameState.getInstance().setPlayFlow(PLAY_FLOW.PLAY);
        }
    }

}
