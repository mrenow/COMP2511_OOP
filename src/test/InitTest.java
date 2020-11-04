package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.engine.*;


public class InitTest {
    GameController game;
	@BeforeEach
    public void setupGame() throws Exception {
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    }
    @Test
    /**
     * tests if the game is setup correctly
     */
    public void testConfig() {
    	Faction rome = game.getCurrentTurn();
    	Faction gaul = game.getFactions().get(1);

    	// Assert that current turn is rome.
    	assertEquals(FactionType.ROME, rome.getType());
    	
    	for (int i = 0; i < 10; i ++) {
	    
	    	assertNull(game.checkVictory());
	    	
	    	// Test turn order and that factions exist
	    	assertEquals(FactionType.ROME, game.getFactions().get(0).getType());
	    	assertEquals(FactionType.GAUL, game.getFactions().get(1).getType());
	    	
	    	Province P1 = game.getProvince("P1");
	    	Province P2 = game.getProvince("P2");
	    	Province P3 = game.getProvince("P3");
	    	Province P4 = game.getProvince("P4");
	    	
	    	// All provinces exist
	    	assertNotNull(P1);
	    	assertNotNull(P2);
	    	assertNotNull(P3);
	    	assertNotNull(P4);
	    	
	    	
	    	assertEquals(4, game.getProvinces(null).size());
	
	    	// Provinces are correctly connected
	    	TestUtil.assertCollectionEquals(
	    			List.of(P2, P4), P1.getAdjacent());
	    	TestUtil.assertCollectionEquals(
	    			List.of(P2, P4), P3.getAdjacent());
	    	TestUtil.assertCollectionEquals(
	    			List.of(P1, P3), P2.getAdjacent());
	    	TestUtil.assertCollectionEquals(
	    			List.of(P1, P3), P4.getAdjacent());
	
	    	// Correct ownership
	    	TestUtil.assertCollectionEquals(
	    			List.of(P1, P2), rome.getProvinces());
	    	TestUtil.assertCollectionEquals(
	    			List.of(P3, P4), gaul.getProvinces());
	    	game.endTurn();
    	}
    }
    @Test
    public void testTurns() {
    	for (int i = 200; i < 210; i ++) {
	     	assertEquals(i, game.getYear());
    		assertNull(game.endTurn());
	    	assertEquals(FactionType.GAUL, game.getCurrentTurn().getType());

	     	assertEquals(i, game.getYear());
	     	assertNull(game.endTurn());
	    	assertEquals(FactionType.ROME, game.getCurrentTurn().getType());    	
    	}
    }
    @Test
    public void provinceAllocation() throws DataInitializationError {
    	
    	// Tested on 10000 runs without fail, so 50 will do for now.
    	for (int i = 0; i < 50; i ++) {
        	// make a new random game
        	game = new GameController("src/unsw/ui/province_id_adjacent.json",
        			"src/unsw/ui/landlocked_provinces.json",
        			List.of(FactionType.ROME, FactionType.GAUL));
        	Faction rome = game.getCurrentTurn();
        	Faction gaul = game.getFactions().get(1);
        	// each faction gets a quarter of the available provinces
        	int numStartProvinces = (int)(GameController.STARTING_DENSITY*game.getNumProvinces()/2);
        	assertEquals(numStartProvinces, rome.getNumProvinces());
        	assertEquals(numStartProvinces, gaul.getNumProvinces());
        	for (Province p : game.getProvinces(null)) {
        		if(p.getOwner() != Faction.NO_ONE) {
        			continue;
        		}
        		assertFalse(p.getUnits().isEmpty());
        	}
    	}
    }
}
