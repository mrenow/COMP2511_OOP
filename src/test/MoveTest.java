package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    
    
    

	@Test
	public void basicMovement() throws DataInitializationException {
		game = new GameController("src/test/testAdjacencey_movement", null, "src/test/testOwnership_movement");
		
		
		
		
	}
	public void cannotMoveToProvinces(){
		
		
	}
	
	public void movementWithRoads() {
		
		
	}
	
		
	private int intMax(int a, int b) {
		return a > b ? a : b;
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
