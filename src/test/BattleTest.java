package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.engine.BattleResult;
import unsw.engine.Battle;
import unsw.engine.DataInitializationError;
import unsw.engine.Faction;
import unsw.engine.GameController;
import unsw.engine.GlobalRandom;
import unsw.engine.Province;
import unsw.engine.Unit;

public class BattleTest {
    private GameController game;
    private Faction rome;
    private Faction gaul;

    private List<Unit> romanUnits;
    private List<Unit> gallicUnits;
    private String tempPath = "src/test/tempFile.json";
    /*
     * GU(0), RU(0) : invincible troops, should always win, but should draw against each other.
     * 
     * 
     */
    @BeforeEach
    public void setupGame() throws Exception{
    	game = GameController.loadFromSave("src/test/testSave_Battle.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    	romanUnits = P(1).getUnits();
    	gallicUnits = P(4).getUnits();
    }
    
    
    @Test
    public void invadeEmpty() {
    	// move romans to province 2 and invade province 3 (empty)
    	game.move(List.of(RU(1)), P(2));
    	BattleResult result = game.invade(P(2).getUnits(), P(3));
    	int oldHealth = RU(1).getHealth();
    	assertEquals(BattleResult.WIN, result);
    	
    	// Test that ownership has been transferred
    	TestUtil.assertCollectionEquals(List.of(P(1), P(2), P(3)), rome.getProvinces());
    	TestUtil.assertCollectionEquals(List.of(P(4)), gaul.getProvinces());
    	assertEquals(rome, P(3).getOwner());
    	
    	// Test that units have not been damaged.
    	assertEquals(oldHealth, RU(1).getHealth());
    	assertTrue(RU(1).isAlive());
    	assertIterableEquals(List.of(RU(1)), P(3).getUnits()); 
    }

    @Test
    public void basicEngagement() {
    	// move troops to 1v1
    	GlobalRandom.init(1462456408965413558L);
    	game.move(List.of(RU(0)), P(2));
    	game.endTurn();
    	game.move(List.of(GU(1)), P(3));
    	assertTrue(GU(1).canAttack());
    	assertIterableEquals(game.getAttackable(List.of(GU(1))), List.of(P(2)));
    	// Face off:
    	assertEquals(BattleResult.LOSE, game.invade(List.of(GU(1)), P(2)));
    	assertFalse(GU(1).isAlive());
    	assertTrue(RU(0).isAlive());
    	assertIterableEquals(List.of(), P(3).getUnits());
    	assertIterableEquals(List.of(RU(0)), P(2).getUnits());
    	
    	game.move(List.of(GU(0)), P(3));
    	//assertIterableEquals(List.of(P(2)),  game.getAttackable(P(3)));
    	assertEquals(BattleResult.DRAW, game.invade(List.of(GU(0)), P(2)));
    	assertTrue(RU(0).isAlive());
    	assertTrue(GU(0).isAlive());
    	// Unit which has attacked cannot attack again.
    	assertFalse(GU(0).canAttack());
    	
    	assertIterableEquals(List.of(GU(0)), P(3).getUnits());
    	assertIterableEquals(List.of(RU(0)), P(2).getUnits());
    	//System.out.println(GlobalRandom.getLog());
    	// Movement points of G0 should be 0.
    	assertEquals(0,GU(0).getMovPoints());
    	
    	game.endTurn();    	
    }
    @Test 
    public void legionaryEagleDebuff() throws IOException, DataInitializationError {
    	GlobalRandom.init(1088668973864283015L);
    	// get ready to curbstomp two eagle units
    	game.move(List.of(RU(4),RU(5)), P(2));
    	game.endTurn();
    	game.move(List.of(GU(0)), P(3));
   
    	assertEquals(BattleResult.WIN, game.invade(List.of(GU(0)), P(2)));
    
    	// eagles are lost
    	assertFalse(RU(4).isAlive());
    	assertFalse(RU(5).isAlive());
    	assertIterableEquals(List.of(P(2)),rome.getLostEagleProvinces());
    	assertEquals(2, rome.getNumLostEagles());
    	
    	// test that saving can handle eagles
    	game.saveGame(tempPath);
    	
    	GameController game2 = GameController.loadFromSave(tempPath);
    	Faction rome2 = game2.getFactions().get(0);
    	assertIterableEquals(List.of(game2.getProvince("P2")),rome2.getLostEagleProvinces());
    	assertEquals(2, rome2.getNumLostEagles());
    	
    	game.endTurn();
    	
    	List<Unit> romanArmy = P(1).getUnits();
    	
    	// Check that eagles return once the settlement is recaptured.
    	assertEquals(BattleResult.WIN, game.invade(romanArmy, P(2)));
    	assertIterableEquals(List.of(), rome.getLostEagleProvinces());
    	assertEquals(0, rome.getNumLostEagles());	
    }
    
    
    @Test
    public void basicRawBattle() throws FileNotFoundException {
    	GlobalRandom.init();
    	// possibilites : 
    	Battle b  = new Battle(List.of(RU(0)), List.of(GU(4)));

		BattleResult result = b.getResult().getResult();
    	b.printLog(new PrintStream(new File("src/test/basicRawBattle.log")));
    }
    

	private Province P(int index) {
		return game.getProvince("P" + index);
	}	
	
	private Unit RU(int index) {
		return romanUnits.get(index);
	}
	private Unit GU(int index) {
		return gallicUnits.get(index);
	}
	
	@AfterEach
	public void cleanUp() throws IOException {
		if(Files.exists(Path.of(tempPath))){
			Files.delete(Path.of(tempPath));
		}
	}
	// used to set up load files
    public static void main(String[] args) throws Exception {
    	GameController.loadFromSave("src/test/testLoad_BattleNormal.json").saveGame("src/test/testSave_BattleNormal.json");;
    	GameController.loadFromSave("src/test/testLoad_BattleNormal2.json").saveGame("src/test/testSave_BattleNormal2.json");
    	GameController.loadFromSave("src/test/testLoad_BattleElephants.json").saveGame("src/test/testSave_BattleElephants.json");
	}
}
