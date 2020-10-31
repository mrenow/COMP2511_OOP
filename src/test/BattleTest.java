package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.AttackInfo;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Unit;

public class BattleTest {
    private GameController game;
    private Faction rome;
    private Faction gaul;

    private List<Unit> allUnits;
    // P1, P4 have 4 default units each
    @BeforeEach
    public void setupGame() throws Exception{
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    	allUnits = P(1).getUnits();
    	allUnits.addAll(P(4).getUnits());
    }
    
    @Test
    public void invadeEmpty() {
    	// move romans to province 2 and invade province 3 (empty)
    	game.move(List.of(RU(1)), P(2));
    	AttackInfo result = game.invade(P(2).getUnits(), P(3));
    	int oldHealth = RU(1).getHealth();
    	assertEquals(AttackInfo.WIN, result);
    	
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
    	// invade province 4:
    	RU(1);
    	
    }

	private Province P(int index) {
		return game.getProvince("P" + index);
	}	
	
	private Unit RU(int index) {
		return allUnits.get(index);
	}
	private Unit GU(int index) {
		return allUnits.get(index + 4);
	}
	
	@AfterEach
	public void cleanUp() {
		
		
	}
}
