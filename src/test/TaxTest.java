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
     * @throws DataInitializationException 
     * @throws Exception
     */

	@Test
    public void testTaxIncome() throws DataInitializationException{
    	game = GameController.loadFromSave("src/test/testSave_wealthGen.json");
    	player = game.getCurrentTurn(); 
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
			assertEquals(wealth1 + wealth2 + wealth3 + wealth4, player.getTotalWealth());
			assertEquals(expected_gold, player.getGold());
		}
    }
	
	@Test
	public void changeTaxRate() throws DataInitializationException {
    	game = GameController.loadFromSave("src/test/testSave_wealthGen.json");
    	player = game.getCurrentTurn(); 
		Province pVeryHigh = game.getProvince("P4");
		game.endTurn();
	
		assertEquals(70, pVeryHigh.getWealth());
		game.setTax(pVeryHigh, TaxLevel.LOW_TAX);
		game.endTurn();
		assertEquals(80, pVeryHigh.getWealth());
		game.setTax(pVeryHigh, TaxLevel.HIGH_TAX);
		game.setTax(pVeryHigh, TaxLevel.NORMAL_TAX);
		game.setTax(pVeryHigh, TaxLevel.VERY_HIGH_TAX);
		game.endTurn();
		assertEquals(50, pVeryHigh.getWealth());
	}
	public void wealthBuildings() throws DataInitializationException {
    	game = GameController.loadFromSave("src/test/testSave_wealthGen2.json");
    	player = game.getCurrentTurn(); 
		// test that base wealth is added properly
		int tWealth1 = 10;
		int tWealth2 = 10;
		int tWealth3 = 10;
		int tWealth4 = 10;
		
    	assertEquals(20 + tWealth1, game.getProvince("P5").getWealth()); // 30 * 0.1  = 3
		assertEquals(30 + 20 + tWealth2, game.getProvince("P6").getWealth()); // 60 * 0.15 = 9
		assertEquals(30 + tWealth3, game.getProvince("P7").getWealth()); // 40 * 0.2  = 8
		assertEquals(30 + tWealth4, game.getProvince("P8").getWealth()); // 40 * 0.25  = 10
		
		game.endTurn();
		// test that gold is added properly:
		assertEquals(100 + 3 + 9 + 8 + 10, player.getGold());
		// test that wealth is added properly: 

		tWealth1 += 20 + 10; 
		tWealth2 += 20 + 40;
		tWealth3 += 5 - 10;
		tWealth4 += 20 - 30;

    	assertEquals(20 + tWealth1, game.getProvince("P5").getWealth()); // 60 * 0.1  = 6
		assertEquals(30 + 20 + tWealth2, game.getProvince("P6").getWealth()); // 120 * 0.15 = 18
		assertEquals(30 + tWealth3, game.getProvince("P7").getWealth()); // 35 * 0.2  = 7
		assertEquals(30 + tWealth4, game.getProvince("P8").getWealth()); // 30 * 0.25  = 7.5 -> 8
		
		game.endTurn();
		
		assertEquals(100 + 3 + 9 + 8 + 10 + 6 + 18 + 7 + 8, player.getGold());
		// testing that total wealth is not lesss than building wealth, and wealth is still calculated properly.
		tWealth1 += 20 + 10; 
		tWealth2 += 20 + 40;
		assertEquals(20 + tWealth1, game.getProvince("P5").getWealth()); // 90 * 0.1  = 9
		assertEquals(20 + tWealth2, game.getProvince("P6").getWealth()); // 180 * 0.15 = 27
		assertEquals(30, game.getProvince("P7").getWealth()); // 30 * 0.2  = 6
		assertEquals(30, game.getProvince("P8").getWealth()); // 30 * 0.25  = 7.5 -> 8

		assertEquals(100 + 3 + 9 + 8 + 10 + 6 + 18 + 7 + 8 + 9 + 27 + 6 + 8, player.getGold());
	}
		
	private int intMax(int a, int b) {
		return a > b ? a : b;
	}
    
	@AfterEach
	public void cleanUp() {
		
		
	}
	
}