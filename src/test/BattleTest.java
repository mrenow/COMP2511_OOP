package test;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Unit;

public class BattleTest {
    private GameController game;
    private Faction rome;
    private Faction gaul;

    private List<Unit> allUnits;
    @BeforeEach
    public void setupGame() throws Exception{
    	game = new GameController(
    			"src/test/test1adjacency.json",
    			"src/test/test1landlocked.json",
    			"src/test/test1ownership.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    	
    	
    }
    
    @Test
    public void basicEngagement() {
    	
    	
    }

	private Province P(int index) {
		return game.getProvince("P" + index);
	}	
	
	private Unit U(int index) {
		return allUnits.get(index);
	}
	
	@AfterEach
	public void cleanUp() {
		
		
	}
}
