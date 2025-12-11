package src.GamePage;

import src.Connection;
import src.Model.Card;
import src.Model.GameState;
import src.Model.PLAY_FLOW;
import src.Model.User;

import java.util.List;

public class GameManager {
    private static GameManager instance = new GameManager();

    private GameManager() {
    }

    ;

    public static GameManager getInstance() {
        return instance;
    }

    public void decideWhoTake(Card myCard, Card opponentCard) {
        if (myCard.getType().equals(opponentCard.getType())) {
            User winner = myCard.getNumberPower() > opponentCard.getNumberPower() ?
                    GameState.getInstance().getMe() :
                    GameState.getInstance().getOpponent();

            myCard.setOwner(winner);
            opponentCard.setOwner(winner);
            myCard.setTaken(true);
            opponentCard.setTaken(true);

            winner.getTaken_cards().addAll(List.of(myCard, opponentCard));

            if(!winner.isAbleToSeeHandCards()) winner.setAbleToSeeHandCards(true);

            if(GameState.getInstance().getMe().equals(winner)){
                System.out.println("GAZANDIM");
                GameState.getInstance().setPlayFlow(PLAY_FLOW.PLAY);
            }

        } else {

        }
        GamePageUI.getInstace().refreshGrids();
        GamePageUI.getInstace().refreshWest();
    }

}
