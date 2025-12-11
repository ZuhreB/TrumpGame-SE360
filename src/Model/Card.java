package src.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Card implements Serializable {


    String png_address;
    public static final String CLOSED_FACE_ADDRESS ="src/cards/card_close.jpg";
    String number;
    int numberPower;
    String type;
    boolean isClose;
    boolean isTaken=false;
    User owner;
    static ArrayList<Card> allCards = new ArrayList<>();



    public Card(String png_address, String number, boolean isClose, String type) {
        this.png_address = png_address;
        this.number = number;
        this.isClose = isClose;
        this.type = type;
        assignPower();
    }

    public static void initAllCards(){
        // Define the standard suits and ranks for a deck.
        List<String> types = Arrays.asList("clubs", "diamonds", "hearts", "spades");
        List<String> numbers = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "ace", "jack", "king", "queen");

        for (String type : types) {
            for (String number : numbers) {
                String imagePath = "src/cards/" + number + "_of_" + type + ".png";

                // Create a new Card object and add it to the deck.
                // All cards are initialized face-up (isClose = false).
                allCards.add(new Card(imagePath, number, true, type));
            }
        }
    }

    private void assignPower(){
        if (number.equals("jack")) {
            numberPower=11;
        } else if (number.equals("queen")) {
            numberPower=12;
        } else if (number.equals("king")) {
            numberPower=13;
        }else if(number.equals("ace")){
            numberPower=14;
        } else{
            numberPower=Integer.parseInt(number);
        }
    }

    public static void shuffleAllCards(){
        Collections.shuffle(allCards);
    }

    public String getPng_address() {
        return png_address;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isClose() {
        return isClose;
    }

    public static ArrayList<Card> getAllCards() {

        return allCards;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getNumberPower() {
        return numberPower;
    }

    public void setNumberPower(int numberPower) {
        this.numberPower = numberPower;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
