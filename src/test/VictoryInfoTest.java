package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.VictoryInfo;
import unsw.gloriaromanus.VicCondition.*;

public class VictoryInfoTest {

    @Test
    public void victoryInfo() {
        VicLeaf l1 = new VicLeaf("TREASURY");
        VicLeaf l2 = new VicLeaf("CONQUEST");
        VicLeaf l3 = new VicLeaf("WEALTH");
        VicComposite c1 = new VicComposite("AND");
        VicComposite c2 = new VicComposite("OR");
        c2.addSubVic(l1);
        c2.addSubVic(l2);
        c1.addSubVic(l3);
        c1.addSubVic(c2);
        VictoryInfo vic = new VictoryInfo(c1);
        assertEquals(false, vic.isVictory());
        vic.setWealth(1.0);
        assertEquals(false, vic.isVictory());
        vic.setConquest(1.0);
        assertEquals(true, vic.isVictory());
        vic.setWealth(0.5);
        assertEquals(false, vic.isVictory());
        vic.setTreasury(1.0);
        assertEquals(false, vic.isVictory());
        vic.setWealth(1.0);
        assertEquals(true, vic.isVictory());
        vic.setConquest(0.5);
        assertEquals(true, vic.isVictory());
    }
    
    @Test
    public void updateVictoryInfo(){
        
    }
}
