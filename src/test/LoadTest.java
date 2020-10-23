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
    public void setupGame() {
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    }
    @Test
    public void testFactionSerialization(){

    	
    }
    public static void main(String[] args) {
    	
    	ObjectMapper om = new ObjectMapper();
		try {
			Faction f = new Faction(FactionType.ROME, 10000, new ArrayList<Province>());
	    	String json = om.writeValueAsString(f);
	    	System.out.println(json);
	    	Faction f2 = om.readValue(json, f.getClass());
	    	System.out.println(f.getGold());
	    	System.out.println(f2.getGold());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
