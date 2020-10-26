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


public class TaxTest {
    private GameController game;
    private Faction player;
    
    /**
     * one player with 100 gold, four provinces each with 100 wealth and different tax levels.
     * See file for more detailed starting conditions.
     * @throws Exception
     */
    @BeforeEach
    public void setupGame() throws Exception{
    	game = GameController.loadFromSave("src/test/testSave_wealthGen.json");
    	player = game.getCurrentTurn(); 
    }
	@Test
    public void testTaxIncome(){
		assertEquals(100, player.getGold());

		int wealth1 = 100;
		int wealth2 = 100;
		int wealth3 = 100;
		int wealth4 = 100;
		int expected_gold = 100;
		for (int i = 1; i <= 11; i++) {
			game.endTurn();
			expected_gold += Math.round(0.1*wealth1 + 0.15*wealth2 + 0.2*wealth3 + 0.25*wealth4);

			wealth1 = intMax(wealth1 + 10, 0);
			wealth3 = intMax(wealth3 - 10, 0);
			wealth4 = intMax(wealth4 - 30, 0);
			
			// tax levels should have done their work on the province wealths:
			assertEquals(wealth1, game.getProvince("P1").getWealth());
			assertEquals(wealth2, game.getProvince("P2").getWealth());
			assertEquals(wealth3, game.getProvince("P3").getWealth());
			assertEquals(wealth4, game.getProvince("P4").getWealth());
			
			// Money should increase based on previous turn's tax rate:
			assertEquals(expected_gold, player.getGold());
		}
    }
	
	@Test
	public void changeTaxRate() {
		Province pVeryHigh = game.getProvince("P4");
		game.endTurn();
	
		assertEquals(70, pVeryHigh.getWealth());
		game.setTax(pVeryHigh, TaxLevel.LOW_TAX);
		game.endTurn();
		assertEquals(80, pVeryHigh.getWealth());
		game.setTax(pVeryHigh, TaxLevel.HIGH_TAX);
		game.setTax(pVeryHigh, TaxLevel.NORMAL_TAX);
		game.setTax(pVeryHigh, TaxLevel.VERY_HIGH_TAX);
		assertEquals(50, pVeryHigh.getWealth());
		
	}
		
	private int intMax(int a, int b) {
		return a > b ? a : b;
	}
    
	@AfterEach
	public void cleanUp() {
		
		
	}
	
}
