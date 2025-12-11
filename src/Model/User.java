package src.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String nickName;
    int score;
    Role role;
    ArrayList<Card> board_cards= new ArrayList<>();
    ArrayList<Card> hand_cards= new ArrayList<>();
    ArrayList<Card> taken_cards= new ArrayList<>();
    boolean isMyTurn;
    boolean isAbleToSeeHandCards=false;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ArrayList<Card> getBoard_cards() {
        return board_cards;
    }

    public void setBoard_cards(ArrayList<Card> board_cards) {
        this.board_cards = board_cards;
    }

    public ArrayList<Card> getHand_cards() {
        return hand_cards;
    }

    public void setHand_cards(ArrayList<Card> hand_cards) {
        this.hand_cards = hand_cards;
    }

    public ArrayList<Card> getTaken_cards() {
        return taken_cards;
    }

    public void setTaken_cards(ArrayList<Card> taken_cards) {
        this.taken_cards = taken_cards;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public boolean isAbleToSeeHandCards() {
        return isAbleToSeeHandCards;
    }

    public void setAbleToSeeHandCards(boolean ableToSeeHandCards) {
        isAbleToSeeHandCards = ableToSeeHandCards;
    }
}
