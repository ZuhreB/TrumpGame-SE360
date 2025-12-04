package src.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Card {

    Card(){

    }
    String png_address;
    public static final String CLOSED_FACE_ADDRESS ="src/cards/card_close.jpg";
    String number;
    String type;
    boolean isClose;
    User owner;
    static ArrayList<Card> allCards = new ArrayList<>();

    public Card(String png_address, String number, boolean isClose, String type) {
        this.png_address = png_address;
        this.number = number;
        this.isClose = isClose;
        this.type = type;
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
                allCards.add(new Card(imagePath, number, false, type));
            }
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
}
