package src.GamePage;

import src.Connection;
import src.Database.DatabaseManager;
import src.GameLogic;
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


    public static GameManager getInstance() {
        return instance;
    }

    public void decideWhoTake(Card myCard, Card opponentCard,PLAY_FLOW myRole) {
        if (myCard.getType().equals(opponentCard.getType())) {
            winner = myCard.getNumberPower() > opponentCard.getNumberPower() ?
                    GameState.getInstance().getMe() :
                    GameState.getInstance().getOpponent();
        } else if(myCard.getType().equals(GameState.getInstance().getSecilen_trump()) ||
                opponentCard.getType().equals(GameState.getInstance().getSecilen_trump())){
            winner=opponentCard.getType().equals(GameState.getInstance().getSecilen_trump())?
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
        GamePageUI.getInstace().refreshRight();
        GamePageUI.getInstace().refreshScores();
    }

    void afterDecideWhoTakeCard(Card myCard,Card opponentCard){
        int gameId= GameState.getInstance().getDbGameId();
        myCard.setOwner(winner);
        opponentCard.setOwner(winner);
        myCard.setTaken(true);
        opponentCard.setTaken(true);

        winner.getTaken_cards().addAll(List.of(myCard, opponentCard));
        if(!winner.isAbleToSeeHandCards()&&winner==GameState.getInstance().getMe()) winner.setAbleToSeeHandCards(true);

        // bu aşamada kazanan belli olduğu için score u güncelliycem
        // kimin elinde ne kadar kart çok kart varsa o kazanacağı için her kart bir puan gibi düşünüp size'ları vericem
        if(gameId != -1){
            int myScore = GameState.getInstance().getMe().getTaken_cards().size();
            int opponentScore = GameState.getInstance().getOpponent().getTaken_cards().size();
            DatabaseManager.getInstance().updateGameScore(gameId, opponentScore, myScore);
        }
        if(GameFinish()){
            int winnerId=DatabaseManager.getInstance().getWinnerId(gameId);
            DatabaseManager.getInstance().finishGame(gameId,winnerId);
        } else if(GameState.getInstance().getMe().equals(winner)){
            System.out.println("GAZANDIM");
            GameState.getInstance().setPlayFlow(PLAY_FLOW.PLAY);
        }

    }

    public boolean controlIfClickable(Card intedtedCard, Card opponentCard) {
        if (GameState.getInstance().getPlayFlow() == PLAY_FLOW.PLAY_BACK) {
            // 2. Renk Eşleşmesi Kontrolü
            if (intedtedCard.getType().equals(opponentCard.getType())) {
                return true;
            }
            // 3. Elde rakibin renginden var mı kontrolü (Mecburiyet kuralı)
            for (Card card : getPlayableCards()) {
                // Döngü çok şişmesin diye sadece eşleşme bulunursa detay yazdırıyoruz,
                // ama her kartı görmek istersen buraya da print atabilirsin.
                if (card.getType().equals( opponentCard.getType())) {
                    GameLogic.getInstance().showGameMessage("Rakibinizle aynı tipten - " + opponentCard.getType() + " -kart oynamalısınız");
                    return false;
                }
            }
            // 4. Koz Kontrolü (Renk yoksa koz atma zorunluluğu)
            String currentTrump = GameState.getInstance().getSecilen_trump();
            // Eğer oynamak istediğimiz kart koz değilse, elimizde koz var mı diye bakıyoruz
            if (!intedtedCard.getType().equals(currentTrump)) {
                for (Card card : getPlayableCards()) {
                    System.out.println(card.getNumber()+" "+card.getType());
                    if (card.getType().equals(currentTrump)) {
                        GameLogic.getInstance().showGameMessage("Aynı tipten kartınız yok ve kozunuz varsa koz oynamalısınız-" + currentTrump + "-");
                        return false;
                    }
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
                if(!card.isTaken()){
                    playableCards.add(card);
                }
            }
        }
        return playableCards;
    }

    public boolean GameFinish(){
        if(52 == GameState.getInstance().getMe().getTaken_cards().size() +
                GameState.getInstance().getOpponent().getTaken_cards().size()){
            return true;
        }else{
            return false;
        }
    }

}
