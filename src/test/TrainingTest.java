package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
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

public class TrainingTest {
    private GameController game;
    private Faction player;

    /**
     * Player starting with x provinces owned, move units to adjacent province
     * 
     * @throws DataInitializationException
     * @throws Exception 
     */
    @Test
    public void basicTraining() throws DataInitializationException {
        game = GameController.loadFromSave("src/test/testTraining_troop.json");
        player = game.getCurrentTurn();
        ItemType calvary = ItemType.TEST_TROOP;
        Province p1 = game.getProvince("P1");

        
        // Check faction gold
        assertEquals(60, player.getGold());
        // Check number of training slots
        assertEquals(3, p1.getTrainingSlots());

        // Now, train calvary
        game.trainUnit(p1, calvary);
        //System.out.println("im here");
        

        // Check no training slots left, and remaining gold
        assertEquals(50, player.getGold());
        assertEquals(2, p1.getTrainingSlots());

        //
        game.trainUnit(p1, calvary);

        assertEquals(40, player.getGold());
        assertEquals(1, p1.getTrainingSlots());

        game.trainUnit(p1, calvary);

        assertEquals(30, player.getGold());
        assertEquals(0, p1.getTrainingSlots());

    }

    @Test
    public void cancelTraining() throws DataInitializationException {
        game = GameController.loadFromSave("src/test/testTraining_troop.json");
        player = game.getCurrentTurn();
        ItemType calvary = ItemType.HEAVY_CAVALRY;
        Province p1 = game.getProvince("P1");
        // TBD
        //TrainingSlotEntry one = TrainingSlotEntry somethingTBD;

        // Check for full training slots
        assertEquals(3, p1.getTrainingSlots());

        game.trainUnit(p1, calvary);

        // Check -1 on max training slots
        assertEquals(2, p1.getTrainingSlots());

        TrainingSlotEntry u = new TrainingSlotEntry(calvary, 1, p1);
        game.cancelTraining(u);

        // Check training slot back to full
        assertEquals(3, p1.getTrainingSlots());


    }

    @AfterEach
	public void clear() {

    }
    
    public static void main(String[] args) throws DataInitializationException {
        GameController game;
        Faction player;
        game = GameController.loadFromSave("src/test/testTraining_troop.json");
        player = game.getCurrentTurn();
    }
}
