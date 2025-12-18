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
        // 1. Metoda giriş logları: Parametreleri ve genel durumu yazdırıyoruz
        System.out.println("--- DEBUG: controlIfClickable BAŞLADI ---");
        System.out.println("[DEBUG] Oynanmak istenen kart (intedtedCard): " + intedtedCard + " | Tipi: " + (intedtedCard != null ? intedtedCard.getType() : "NULL"));
        System.out.println("[DEBUG] Rakip kart (opponentCard): " + opponentCard + " | Tipi: " + (opponentCard != null ? opponentCard.getType() : "NULL"));
        System.out.println("[DEBUG] Mevcut Oyun Akışı (PlayFlow): " + GameState.getInstance().getPlayFlow());

        if (GameState.getInstance().getPlayFlow() == PLAY_FLOW.PLAY_BACK) {
            System.out.println("[DEBUG] Akış PLAY_BACK modunda. Kurallar kontrol ediliyor...");

            // 2. Renk Eşleşmesi Kontrolü
            if (intedtedCard.getType().equals(opponentCard.getType())) {
                System.out.println("[DEBUG] Kart tipleri EŞLEŞİYOR. Hamle geçerli.");
                System.out.println("--- DEBUG: controlIfClickable BİTTİ (TRUE) ---");
                return true;
            } else {
                System.out.println("[DEBUG] Kart tipleri EŞLEŞMEDİ. Elde mecburi renk var mı bakılıyor...");
            }

            // 3. Elde rakibin renginden var mı kontrolü (Mecburiyet kuralı)
            System.out.println("[DEBUG] Döngü: Eldeki oynanabilir kartlar taranıyor...");
            for (Card card : getPlayableCards()) {
                // Döngü çok şişmesin diye sadece eşleşme bulunursa detay yazdırıyoruz,
                // ama her kartı görmek istersen buraya da print atabilirsin.
                if (card.getType().equals( opponentCard.getType())) {
                    System.out.println("a"); // Senin orijinal logun
                    System.out.println("[DEBUG] KURAL İHLALİ: Elde rakibin renginden (" + opponentCard.getType() + ") kart bulundu: " + card);

                    GameLogic.getInstance().showGameMessage("Rakibinizle aynı tipten - " + opponentCard.getType() + " -kart oynamalısınız");

                    System.out.println("--- DEBUG: controlIfClickable BİTTİ (FALSE - Renk Mecburiyeti) ---");
                    return false;
                }
            }
            System.out.println("[DEBUG] Elde rakibin renginden KART YOK. Koz kontrolüne geçiliyor.");

            // 4. Koz Kontrolü (Renk yoksa koz atma zorunluluğu)
            String currentTrump = GameState.getInstance().getSecilen_trump();
            System.out.println("[DEBUG] Geçerli Koz (Trump): " + currentTrump);

            // Eğer oynamak istediğimiz kart koz değilse, elimizde koz var mı diye bakıyoruz
            if (!intedtedCard.getType().equals(currentTrump)) {
                System.out.println("benim tip "+intedtedCard.getType()+" koz da "+currentTrump);
                System.out.println("[DEBUG] Seçilen kart KOZ DEĞİL. Elde koz var mı diye bakılıyor...");

                for (Card card : getPlayableCards()) {
                    System.out.println(card.getNumber()+" "+card.getType());
                    if (card.getType().equals(currentTrump)) {
                        System.out.println("b"); // Senin orijinal logun
                        System.out.println("[DEBUG] KURAL İHLALİ: Elde KOZ bulundu: " + card + ". Renk yoksa koz oynanmalı.");

                        GameLogic.getInstance().showGameMessage("Aynı tipten kartınız yok ve kozunuz varsa koz oynamalısınız-" + currentTrump + "-");

                        System.out.println("--- DEBUG: controlIfClickable BİTTİ (FALSE - Koz Mecburiyeti) ---");
                        return false;
                    }
                }
                System.out.println("[DEBUG] Elde koz da yok (veya oynama zorunluluğu yok). Hamle serbest.");
                System.out.println("--- DEBUG: controlIfClickable BİTTİ (TRUE) ---");
                return true;
            } else {
                System.out.println("[DEBUG] Oynanmak istenen kart zaten KOZ. Hamle geçerli.");
            }

        } else {
            System.out.println("[DEBUG] Akış PLAY_BACK değil. Doğrudan izin veriliyor.");
        }

        System.out.println("--- DEBUG: controlIfClickable BİTTİ (TRUE - Final) ---");
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
