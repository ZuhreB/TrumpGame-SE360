package src.Model;

public class GameState {
    static User me = new User();
    static User opponent = new User();


    public static void setMe(User me) {
        me = me;
    }

    public static User getMe(){
        return me;
    }

    public static User getOpponent() {
        return opponent;
    }

    public static void setOpponent(User opponent) {
        opponent = opponent;
    }
}
