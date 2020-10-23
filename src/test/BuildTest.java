package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.graalvm.util.CollectionsUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.*;



public class BuildTest {
    GameController game;
	@BeforeEach
    public void setupGame() {
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
    	
    	assertEquals(FactionType.ROME, rome.getType());
    
    	assertNull(game.checkVictory());
    	
    	// Test turn order
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
    	
    	assertNull(game.endTurn());
    	
    	assertEquals(gaul, game.getCurrentTurn());
    }
    
	@Test
    public void testBuild (){
		Faction current = game.getCurrentTurn();
	
		
    }
    
    @Test
    public void blahTest2(){
        Unit u = new Unit();
        assertEquals(u.getNumTroops(), 50);
    }
}
