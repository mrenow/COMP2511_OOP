package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import unsw.gloriaromanus.*;



public class LoadTest {
    GameController game;
	@BeforeEach
    public void setupGame() throws GameInitializationException{
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    }
    @Test
    public void testFactionSerialization(){
    	TestUtil.assertCollectionAttributeEquals(List.of("P1","P2","P3","P4"), game.getProvinces(null), Province::getName);
    	TestUtil.assertCollectionAttributeEquals(List.of("Rome", "Gaul"), game.getFactions(),
    			Faction::getTitle);
    	
    }
}
