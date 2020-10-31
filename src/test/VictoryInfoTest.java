package test;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.VictoryInfo;
import unsw.gloriaromanus.VicCondition.*;

public class VictoryInfoTest {
    private GameController game;

    @BeforeEach
    public void setupGame()throws Exception{
        game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    }

    @Test
    public void victoryInfo() {
        VicLeaf l1 = new VicLeaf("TREASURY");
        VicLeaf l2 = new VicLeaf("CONQUEST");
        VicLeaf l3 = new VicLeaf("WEALTH");
        VicComposite c1 = new VicComposite("AND");
        VicComposite c2 = new VicComposite("OR");
        c2.addSubVic(l1);
        c2.addSubVic(l2);
        c1.addSubVic(l1);
        c1.addSubVic(c2);
        VictoryInfo vic = new VictoryInfo();
    }
}
