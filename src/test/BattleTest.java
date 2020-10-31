package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.AttackInfo;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.GlobalRandom;
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
    	game = GameController.loadFromSave("src/test/testSave_Battle.json");
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
    	// move troops to 1v1
    	GlobalRandom.init();
    	game.move(List.of(RU(0)), P(2));
    	game.endTurn();
    	game.move(List.of(GU(1)), P(3));
    	
    	// Face off:
    	assertEquals(AttackInfo.LOSE, game.invade(List.of(GU(1)), P(2)));
    	assertFalse(GU(1).isAlive());
    	assertTrue(RU(0).isAlive());
    	assertIterableEquals(List.of(), P(3).getUnits());
    	assertIterableEquals(List.of(RU(0)), P(2).getUnits());
    	
    	game.move(List.of(GU(0)), P(3));
    	assertIterableEquals(List.of(P(2)),  game.getAttackable(P(3)));
    	assertEquals(AttackInfo.DRAW, game.invade(List.of(GU(0)), P(2)));
    	assertTrue(RU(0).isAlive());
    	assertTrue(GU(0).isAlive());
    	assertIterableEquals(List.of(GU(0)), P(3).getUnits());
    	assertIterableEquals(List.of(RU(0)), P(2).getUnits());
    	
    	// Movement points of G0 should be 0.
    	assertEquals(0,GU(0).getMovPoints());
    	
    	
    	
    	
    	
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
