package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unsw.engine.BattleResult;
import unsw.engine.Battle;
import unsw.engine.DataInitializationError;
import unsw.engine.Faction;
import unsw.engine.GameController;
import unsw.engine.GlobalRandom;
import unsw.engine.Unit;

public class BigBattleTest {
	private GameController game;
	private Faction rome;
	private Faction gaul;

	private List<Unit> romanUnits;
	private List<Unit> gallicUnits;

	/*
	 * GU(0), RU(0) : invincible troops, should always win, but should draw against
	 * each other.
	 * 
	 * 
	 */
	@BeforeEach
	public void setupGame() throws Exception {
		game = GameController.loadFromSave("src/test/testSave_BattleNormal.json");
		rome = game.getFactions().get(0);
		gaul = game.getFactions().get(1);
		romanUnits = game.getProvince("P1").getUnits();
		gallicUnits = game.getProvince("P4").getUnits();

	}

	@Test
	public void bigEngagementGaulsWin() throws FileNotFoundException {
		GlobalRandom.init(-5826966330945702263L);
		Battle b = new Battle(romanUnits, gallicUnits);
		BattleResult result = b.getResult().getResult();
		assertEquals(BattleResult.LOSE, result);
		assertEquals(BattleResult.WIN, result.defenderView());
		b.printLog(new PrintStream(new File("src/test/bigEngagementGaulsWin.log")));

	}

	@Test
	public void bigEngagementRomansWin() throws FileNotFoundException {
		GlobalRandom.init(-7558078402375791967L);
		Battle b = new Battle(romanUnits, gallicUnits);
		BattleResult result = b.getResult().getResult();
		assertEquals(BattleResult.WIN, result);
		assertEquals(BattleResult.LOSE, result.defenderView());
		b.printLog(new PrintStream(new File("src/test/bigEngagementRomansWin.log")));
	}

	@Test
	public void bigEngagementDraw() throws FileNotFoundException, DataInitializationError {
		GlobalRandom.init(4071336349643094880L);

		Battle b = new Battle(romanUnits, gallicUnits);
		BattleResult result = b.getResult().getResult();
		assertEquals(BattleResult.DRAW, result);
		assertEquals(BattleResult.DRAW, result.defenderView());
		b.printLog(new PrintStream(new File("src/test/bigEngagementDraw.log")));
	}

	// Checks that a battle can be won with 200 engagements
	@Test
	public void bigEngagementWin200() throws FileNotFoundException, DataInitializationError {
		GlobalRandom.init(8729667887509877221L);


		Battle b = new Battle(romanUnits, gallicUnits);
		BattleResult result = b.getResult().getResult();
		assertEquals(BattleResult.WIN, result);
		assertEquals(BattleResult.LOSE, result.defenderView());
		assertEquals(200, b.getNumAttemptedEngagements());
		b.printLog(new PrintStream(new File("src/test/bigEngagementWin200.log")));
	}



	
}
