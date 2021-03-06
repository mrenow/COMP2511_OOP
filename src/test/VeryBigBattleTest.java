package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.engine.BattleResult;
import unsw.engine.Battle;
import unsw.engine.DataInitializationError;
import unsw.engine.Faction;
import unsw.engine.GameController;
import unsw.engine.GlobalRandom;
import unsw.engine.Unit;

public class VeryBigBattleTest {
    private GameController game;
    private Faction rome;
    private Faction gaul;

    private List<Unit> romanUnits;
    private List<Unit> gallicUnits;
    @BeforeEach
    public void setupGame() throws Exception{
    	game = GameController.loadFromSave("src/test/testSave_BattleNormal2.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    	romanUnits = game.getProvince("P1").getUnits();
    	gallicUnits = game.getProvince("P4").getUnits();
    	
    }
    // Checks that a battle that goes on for more than 200 engagements fails.
    @Test
    public void bigEngagementTimeout() throws FileNotFoundException, DataInitializationError {
    	GlobalRandom.init(-8182561916007065045L);
    	
    	Battle b = new Battle(romanUnits, gallicUnits);
		BattleResult result = b.getResult().getResult();
    	assertEquals(BattleResult.DRAW, result);
    	assertEquals(BattleResult.DRAW, result.defenderView());
    	assertEquals(201, b.getNumAttemptedEngagements());
    	b.printLog(new PrintStream(new File("src/test/bigEngagementTimeout.log")));
     }   


}
