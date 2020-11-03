package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.AttackInfo;
import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.DataInitializationException;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.GlobalRandom;
import unsw.gloriaromanus.Unit;

public class ElephantBattleTest {
    private GameController game;
    private Faction rome;
    private Faction gaul;

    private List<Unit> romanUnits;
    private List<Unit> gallicUnits;
    @BeforeEach
    public void setupGame() throws Exception{
    	game = GameController.loadFromSave("src/test/testSave_BattleElephants.json");
    	rome = game.getFactions().get(0);
    	gaul = game.getFactions().get(1);
    	romanUnits = game.getProvince("P1").getUnits();
    	gallicUnits = game.getProvince("P4").getUnits();
    }
    
	// Covers a case where elephants try to attack themselves but instead choose a different opponent
	@Test
	public void elephantsAmok1() throws FileNotFoundException, DataInitializationException {

		GlobalRandom.init(-1272667509544333134L);
		Battle b = new Battle(romanUnits, gallicUnits);
		AttackInfo result = b.getResult();
		assertTrue(70 < b.getNumAttemptedEngagements());
		b.printLog(new PrintStream(new File("src/test/elephantsAmok.log")));
	}
	// Covers a case where elephants attack any allies
	@Test
	public void elephantsAmok() throws FileNotFoundException, DataInitializationException {

		GlobalRandom.init(-5216799251492405668L);
		Battle b = new Battle(List.of(RU(0), RU(1)), List.of(GU(0), GU(1)));
		AttackInfo result = b.getResult();
		b.printLog(new PrintStream(new File("src/test/elephantsAmok.log")));
	}
	private Unit RU(int index) {
		return romanUnits.get(index);
	}

	private Unit GU(int index) {
		return gallicUnits.get(index);
	}

    public static void main(String[] args) throws Exception {
    	while(true) {
    		GlobalRandom.init();
	    	try {
		    	ElephantBattleTest t = new ElephantBattleTest();
		    	t.setupGame();
		    	t.elephantsAmok();
	    	}catch(Error e) {
	    		continue;
	    	}
	    	System.out.println(GlobalRandom.getSeed());  
	    	break;
    	}
	}
    
    
}
