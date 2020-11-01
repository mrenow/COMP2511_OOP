package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.AttackInfo;
import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.GlobalRandom;
import unsw.gloriaromanus.Unit;

public class BigBattleTest {
    private GameController game;
    private Faction rome;
    private Faction gaul;

    private List<Unit> romanUnits;
    private List<Unit> gallicUnits;
    
    /*
     * GU(0), RU(0) : invincible troops, should always win, but should draw against each other.
     * 
     * 
     */
    @BeforeEach
    public void setupGame() throws Exception{
    	game = GameController.loadFromSave("src/test/testSave_BattleNormal.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    	romanUnits = game.getProvince("P1").getUnits();
    	gallicUnits = game.getProvince("P4").getUnits();
    	
    }
    
    @Test
    public void bigEngagement() throws FileNotFoundException {
    	GlobalRandom.init();
    	Battle b = new Battle(romanUnits, gallicUnits);
    	AttackInfo result = b.getResult();
    	b.printLog(new PrintStream(new File("src/test/bigEngagement.log")));
    	System.out.println(result);
    	
    }
    
}
