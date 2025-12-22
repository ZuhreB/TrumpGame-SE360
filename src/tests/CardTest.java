package src.tests;

import org.junit.Test;
import static org.junit.Assert.*;
import src.Model.Card;

public class CardTest {

    @Test
    public void testCardPowerAssignment() {
        Card seven = new Card("path/to/img", "7", false, "hearts");
        assertEquals( 7, seven.getNumberPower());
    }

    @Test
    public void testCardInitialization() {
        Card card = new Card("test.png", "10", true, "spades");
        assertTrue(card.isClose());
    }
}
