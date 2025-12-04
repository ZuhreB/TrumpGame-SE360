package src.Model;

public class GameState {
     User me = new User();
     User opponent = new User();
     private static GameState instance= new GameState();

     private GameState(){}

    public  void setMe(User me) {
        me = me;
    }

    public  User getMe(){
        return me;
    }

    public  User getOpponent() {
        return opponent;
    }

    public  void setOpponent(User opponent) {
        opponent = opponent;
    }

    public static GameState getInstance() {
        return instance;
    }
}
