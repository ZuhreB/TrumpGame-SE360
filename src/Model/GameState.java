package src.Model;

public class GameState {
    public String getSecilen_trump() {
        return secilen_trump;
    }

    public void setSecilen_trump(String secilen_trump) {
        this.secilen_trump = secilen_trump;
    }

    User me = new User();
     User opponent = new User();
     private String secilen_trump;
     private static GameState instance= new GameState();

     private GameState(){}

    public  void setMe(User me) {
        this.me = me;
    }

    public  User getMe(){
        return me;
    }

    public  User getOpponent() {
        return opponent;
    }

    public  void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    public static GameState getInstance() {
        return instance;
    }

    public void makeAllCardsVisible(){
        for(int i=0;i<me.board_cards.size();i++){
            me.board_cards.get(i).setClose(false);
            opponent.board_cards.get(i).setClose(false);
        }
    }
}
