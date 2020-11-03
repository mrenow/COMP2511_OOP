package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
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



public class BuildTest {
	private GameController game;
	private Faction player;

	public void basicBuildTest() throws DataInitializationException {
		game = GameController.loadFromSave("src/test/testBuildInfras.json");
		player = game.getCurrentTurn();
		ItemType infras = ItemType.TEST_BUILDING;
		Province p1 = game.getProvince("P1");
		
		// Check faction gold and buildingslots
		
		assertEquals(60, player.getGold());
		assertEquals(2, p1.getBuildingSlots());

		// Build Infrastructure
		game.buildInfrastructure(p1, infras);

		// Check remaining gold and slot
		assertEquals(40, player.getGold());
		assertEquals(1, p1.getBuildingSlots());

	}

	public void cancelBuildTest() throws DataInitializationException {
		game = GameController.loadFromSave("src/test/testBuildInfras.json");
		player = game.getCurrentTurn();
		ItemType infras = ItemType.TEST_BUILDING;
		Province p1 = game.getProvince("P1");
		
		// build -> cancel
		assertEquals(2, p1.getBuildingSlots());

		game.buildInfrastructure(p1, infras);
		assertEquals(1, p1.getBuildingSlots());

		// Cancel
		BuildingSlotEntry u = new BuildingSlotEntry(infras, 1);
		game.cancelInfrastructure(u);

		// Check slots again
		assertEquals(2, p1.getBuildingSlots());

	}


    //private Faction rome;
    //private Faction gaul;
    /*
    @BeforeEach
    public void setupGame() throws Exception{
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    }
	*/
	@AfterEach
	public void cleanUp() {
		
		
	}
	
}
