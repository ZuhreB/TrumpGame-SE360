package src.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import src.GamePage.GameManager;
import src.Model.Card;
import src.Model.GameState;
import src.Model.PLAY_FLOW;
import src.Model.Role;
import src.Model.User;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameManagerTest {

    @Before
    public void setUp() {
        GameState.getInstance().setMe(new User());
        GameState.getInstance().setOpponent(new User());
        GameState.getInstance().setTurnStep(1);

        GameState.getInstance().getMe().setNickName("Tester");
        GameState.getInstance().getMe().setRole(Role.HOST);

        GameState.getInstance().getOpponent().setNickName("Opponent");
        GameState.getInstance().getOpponent().setRole(Role.GUEST);

        ArrayList<Card> myBoard = new ArrayList<>();
        ArrayList<Card> opBoard = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            myBoard.add(new Card("src/cards/2_of_clubs.png", "2", false, "clubs"));
            opBoard.add(new Card("src/cards/3_of_clubs.png", "3", false, "clubs"));
        }
        GameState.getInstance().getMe().setBoard_cards(myBoard);
        GameState.getInstance().getOpponent().setBoard_cards(opBoard);

        GameState.getInstance().getMe().setHand_cards(new ArrayList<>());
        GameState.getInstance().getOpponent().setHand_cards(new ArrayList<>());
    }

    @After
    public void tearDown() {
        GameState.getInstance().getMe().getTaken_cards().clear();
        GameState.getInstance().getMe().getBoard_cards().clear();
        GameState.getInstance().getMe().getHand_cards().clear();
        GameState.getInstance().getOpponent().getTaken_cards().clear();
        GameState.getInstance().getOpponent().getBoard_cards().clear();
        GameState.getInstance().getOpponent().getHand_cards().clear();
    }

    @Test
    public void testGameFinish() {
        assertFalse(GameManager.getInstance().GameFinish());

        for (int i = 0; i < 26; i++) {
            GameState.getInstance().getMe().getTaken_cards().add(new Card("img", "1", true, "type"));
            GameState.getInstance().getOpponent().getTaken_cards().add(new Card("img", "1", true, "type"));
        }
        assertTrue(GameManager.getInstance().GameFinish());
    }

    @Test
    public void testControlIfClickableCase1() {
        GameState.getInstance().setPlayFlow(PLAY_FLOW.PLAY_BACK);
        Card opponentCard = new Card("img", "10", false, "hearts");
        Card myCardSameColor = new Card("img", "5", false, "hearts");
        boolean result = GameManager.getInstance().controlIfClickable(myCardSameColor, opponentCard);
        assertTrue(result);
    }

    @Test
    public void testControlIfClickableCase2() {
        GameState.getInstance().setPlayFlow(PLAY_FLOW.PLAY_BACK);
        Card opponentCard = new Card("img", "10", false, "hearts");
        GameState.getInstance().getMe().getBoard_cards().set(10, new Card("img", "2", false, "hearts"));

        Card myCardDiffColor = new Card("img", "5", false, "spades");
        boolean result = GameManager.getInstance().controlIfClickable(myCardDiffColor, opponentCard);
        assertFalse(result);
    }

    @Test
    public void testDecideWhoTakeCase1() {
        Card myCard = new Card("img", "ace", false, "spades"); // Power 14
        Card opCard = new Card("img", "king", false, "spades"); // Power 13

        Card winningCard = GameManager.getInstance().decideWhoTake(myCard, opCard, PLAY_FLOW.PLAY);

        assertEquals(myCard, winningCard);
        assertTrue(GameState.getInstance().getMe().getTaken_cards().contains(myCard));
        assertTrue(GameState.getInstance().getMe().getTaken_cards().contains(opCard));
    }

    @Test
    public void testDecideWhoTakeCase2() {
        GameState.getInstance().setSecilen_trump("hearts");

        Card myCard = new Card("img", "2", false, "clubs");
        Card opCard = new Card("img", "2", false, "hearts"); // Trump

        Card winningCard = GameManager.getInstance().decideWhoTake(myCard, opCard, PLAY_FLOW.PLAY);
        assertEquals(opCard, winningCard);
        assertEquals(GameState.getInstance().getOpponent(), opCard.getOwner());
    }
}
