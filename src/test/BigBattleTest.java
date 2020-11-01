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

import unsw.gloriaromanus.AttackInfo;
import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.DataInitializationException;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GameController;
import unsw.gloriaromanus.GlobalRandom;
import unsw.gloriaromanus.Unit;

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
		AttackInfo result = b.getResult();
		assertEquals(AttackInfo.LOSE, result);
		assertEquals(AttackInfo.WIN, result.defenderView());
		b.printLog(new PrintStream(new File("src/test/bigEngagementGaulsWin.log")));

	}

	@Test
	public void bigEngagementRomansWin() throws FileNotFoundException {
		GlobalRandom.init(-7558078402375791967L);
		Battle b = new Battle(romanUnits, gallicUnits);
		AttackInfo result = b.getResult();
		assertEquals(AttackInfo.WIN, result);
		assertEquals(AttackInfo.LOSE, result.defenderView());
		b.printLog(new PrintStream(new File("src/test/bigEngagementRomansWin.log")));
	}

	@Test
	public void bigEngagementDraw() throws FileNotFoundException, DataInitializationException {
		GlobalRandom.init(4071336349643094880L);

		Battle b = new Battle(romanUnits, gallicUnits);
		AttackInfo result = b.getResult();
		assertEquals(AttackInfo.DRAW, result);
		assertEquals(AttackInfo.DRAW, result.defenderView());
		b.printLog(new PrintStream(new File("src/test/bigEngagementDraw.log")));
	}

	// Checks that a battle can be won with 200 engagements
	@Test
	public void bigEngagementWin200() throws FileNotFoundException, DataInitializationException {
		GlobalRandom.init(8729667887509877221L);


		Battle b = new Battle(romanUnits, gallicUnits);
		AttackInfo result = b.getResult();
		assertEquals(AttackInfo.WIN, result);
		assertEquals(AttackInfo.LOSE, result.defenderView());
		assertEquals(200, b.getNumAttemptedEngagements());
		b.printLog(new PrintStream(new File("src/test/bigEngagementWin200.log")));
	}



	
}
