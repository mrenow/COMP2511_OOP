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



public class LoadTest {
    private GameController game;

	private String filename;

	private static int numFiles = 0;
	@BeforeEach
    public void setupGame() throws Exception{
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    	filename = String.format("src/test/loadTestTemp%d.json", numFiles++);
    	
    	Files.deleteIfExists(Path.of(filename));
    	Files.createFile(Path.of(filename));
    	 
    }
	@Test
    public void loadSaveGame() throws DataInitializationException, IOException{
		TestUtil.assertCollectionAttributeEquals(List.of("P1","P2","P3","P4"), game.getProvinces(null), Province::getName);
    	TestUtil.assertCollectionAttributeEquals(List.of("Rome", "Gaul"), game.getFactions(), Faction::getTitle);
		game.saveGame(filename);
		GameController newGame = GameController.loadFromSave(filename);
    	TestUtil.assertCollectionAttributeEquals(List.of("P1","P2","P3","P4"), game.getProvinces(null), Province::getName);
    	TestUtil.assertCollectionAttributeEquals(List.of("Rome", "Gaul"), game.getFactions(), Faction::getTitle);
		
    }
	
	@AfterEach
	public void cleanUp() {
		
		
	}
	
}
