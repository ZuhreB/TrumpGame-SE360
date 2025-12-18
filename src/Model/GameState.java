package src.Model;

import src.GamePage.GamePageUI;

public class GameState {
    User me = new User();
    User opponent = new User();
    private String secilen_trump;
    private PLAY_FLOW playFlow;
    private int gameId= -1;// database de game_id nin tuttuğu değer her hamlede bu id ye göre eklencek
    private int turnStep=1;
    private int hostId=-1;
    private int guestId=-1;
    private static GameState instance= new GameState();

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getTurnStep() {
        return turnStep;
    }

    public void setTurnStep(int turnStep) {
        this.turnStep = turnStep;
    }


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

    public int getDbGameId() {
        return gameId;
    }

    public void setDbGameId(int dbGameId) {
        this.gameId = dbGameId;
    }
    public String getSecilen_trump() {
        return secilen_trump;
    }


    public void setSecilen_trump(String secilen_trump) {
        this.secilen_trump = secilen_trump;
    }

    public void makeAllCardsVisible(){
        for(int i=0;i<me.board_cards.size();i++){
            me.board_cards.get(i).setClose(false);
            opponent.board_cards.get(i).setClose(false);
        }
    }

    public PLAY_FLOW getPlayFlow() {
        return playFlow;
    }

    public void setPlayFlow(PLAY_FLOW playFlow) {
        this.playFlow = playFlow;
        GamePageUI.getInstace().refreshWest();
    }
}
