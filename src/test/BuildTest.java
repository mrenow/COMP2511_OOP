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



public class BuildTest {
    private GameController game;
    private Faction rome;
    private Faction gaul;
    
    @BeforeEach
    public void setupGame() throws Exception{
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    }
	
	@AfterEach
	public void cleanUp() {
		
		
	}
	
}
