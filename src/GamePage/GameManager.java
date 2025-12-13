package src.GamePage;

import src.Connection;
import src.Model.Card;
import src.Model.GameState;
import src.Model.PLAY_FLOW;
import src.Model.User;

import java.util.ArrayList;
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

    public boolean controlIfClickable(Card intedtedCard,Card opponentCard){
        if(GameState.getInstance().getPlayFlow()==PLAY_FLOW.PLAY_BACK){
            if(intedtedCard.getType()==opponentCard.getType()) return true;

            //Control whether ı have the type of opponent card, if ı have ı can not play any other type
            for(Card card:getPlayableCards()){
                if(card.getType()==opponentCard.getType()) return false;
            }

            //then if intented card is not trump but ı have trump then it returns false
            if(intedtedCard.getType()!=GameState.getInstance().getSecilen_trump()){
                for(Card card:getPlayableCards()){
                    if(card.getType()==GameState.getInstance().getSecilen_trump()) return false;
                }
                return true;
            }

        }
        return true;
    }

    public List<Card> getPlayableCards(){
        List<Card> playableCards = new ArrayList<>();
        List<Card> myBoardCards =GameState.getInstance().getMe().getBoard_cards();

        for(int i = 10;i<20;i++){
            if(myBoardCards.get(i).isTaken()==false){
                playableCards.add(myBoardCards.get(i));
            }else if(myBoardCards.get(i-10).isTaken()==false){
                playableCards.add(myBoardCards.get(i-10));
            }
        }
        if(GameState.getInstance().getMe().isAbleToSeeHandCards()){
            for(Card card:GameState.getInstance().getMe().getHand_cards()){
                playableCards.add(card);
            }
        }
        return playableCards;
    }

}
