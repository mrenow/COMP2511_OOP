package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void build (){
        assertEquals("a", "b");
        String s;
    }
    
    @Test
    public void blahTest2(){
        Unit u = new Unit();
        assertEquals(u.getNumTroops(), 50);
    }
}
