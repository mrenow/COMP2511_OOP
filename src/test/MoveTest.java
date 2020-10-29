package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import unsw.gloriaromanus.*;

public class MoveTest {
    private GameController game;
    private Faction player;
    private List<Unit> allUnits;
    
    public void setup() throws DataInitializationException {
    	game = new GameController("src/test/testAdjacency_movement.json", null, "src/test/testOwnership_movement.json");
		player = game.getCurrentTurn();
		allUnits = P(1).getUnits();
    }

	@Test
	public void destinations(){
		// testing basic destination tracking
		TestUtil.assertCollectionEquals(List.of(P(2), P(3), P(4), P(5), P(6), P(7)), game.getDestinations(List.of(U(1))));
		TestUtil.assertCollectionEquals(List.of(P(2), P(3), P(4), P(5), P(6), P(7), P(8)), game.getDestinations(List.of(U(3), U(2))));
		
		// tetsing basic
		List<Unit> batch1 = List.of(U(0), U(3));
		Collection<Province> dest = game.getDestinations(batch1);
		TestUtil.assertCollectionEquals(List.of(P(2), P(6), P(7)), dest);
		
		game.move(batch1, P(2));
		
		// Check that units have moved
		TestUtil.assertCollectionEquals(List.of(U(1), U(2)), P(1).getUnits());
		TestUtil.assertCollectionEquals(List.of(U(0), U(3)), P(2).getUnits());
		
		// no more locations for batch
		TestUtil.assertCollectionEquals(List.of(), game.getDestinations(batch1));
		
		// Check that unit 3 still has 3 movement points left:
		assertEquals(3, U(3).getMovPoints());
		TestUtil.assertCollectionEquals(List.of(P(1), P(6), P(7), P(3), P(4)), game.getDestinations(List.of(U(3))));

		
		// Move unit 3 to 4. this could be done in three ways, two of which are longer and equivalent
		game.move(List.of(U(3)), P(4));
		
		assertEquals(0, U(3).getMovPoints());
		
		// Check that 3 has moved
		TestUtil.assertCollectionEquals(List.of(U(0)), P(2).getUnits());
		TestUtil.assertCollectionEquals(List.of(U(3)), P(4).getUnits());
		
		// Check that unit 3 has 1 movement point left and not 0:
		assertEquals(1, U(3).getMovPoints());
		TestUtil.assertCollectionEquals(List.of(P(7), P(3), P(5), P(8)), game.getDestinations(List.of(U(3))));
		
		// Check that all other provinces have the correct units

		TestUtil.assertCollectionEquals(List.of(U(1), U(2)), P(1).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(3).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(5).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(6).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(7).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(8).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(9).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(10).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(11).getUnits());
		TestUtil.assertCollectionEquals(List.of(), P(12).getUnits());
		
		game.endTurn();
		// Check that all movement points are regained.
		assertEquals(4, U(3).getMovPoints());
		TestUtil.assertCollectionEquals(List.of(P(1), P(2), P(3), P(5), P(6), P(7), P(8), P(9), P(10), P(11)), game.getDestinations(List.of(U(3))));
	}
	
	@Test
	public void movementWithEnemy(){
		// disown middle province
		game.disownProvince(P(7));
		// check that movement updates accordingly: 8 and 7 are unreachable.
		TestUtil.assertCollectionEquals(List.of(P(2), P(3), P(4), P(5), P(6)), game.getDestinations(List.of(U(2))));
		
		game.disownProvince(P(2));
		// now 2, 3 unreachable
		TestUtil.assertCollectionEquals(List.of(P(4), P(5), P(6)), game.getDestinations(List.of(U(2))));
		
		// Move unit 3 to province 3. all points should be expended as they had to take a detour.
		TestUtil.assertCollectionEquals(List.of(P(4), P(5), P(6), P(3), P(8)), game.getDestinations(List.of(U(3))));
		game.move(List.of(U(3)), P(3));
		assertEquals(0, U(3).getMovPoints());
	}
	
	// eh ceebs.
	public void movementWithRoads() {
		

		
	}

	private Province P(int index) {
		return game.getProvince("P" + index);
	}	
	private Unit U( int index) {
		return allUnits.get(index);
	}
	@AfterEach
	public void cleanUp() {
		
		
	}
    /*
     * Player starting with x provinces owned, move units to adjacent province
     * 
     * @throws Exception
     
    @BeforeEach
    public void setupGame() throws Exception{
        // TODO
    	game = GameController.loadFromSave("");
    	player = game.getCurrentTurn(); 
    }

    @Test
    public void testMoveUnits() {
        Province p2 = game.getProvince("P2");
        Province p3 = game.getProvince("P3");
        testList = new ArrayList<Unit>();

        // TODO
        testList.add(HEAVY_CALVARY);
        game.endTurn();

        game.move(testList, p3);
        game.endTurn();
        System.out.println(p3.getUnits().contains(HEAVY_CALVARY));
    }
     */
}
