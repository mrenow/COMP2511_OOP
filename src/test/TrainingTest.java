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
        ItemType calvary = ItemType.HEAVY_CAVALRY;
        Province p1 = game.getProvince("P1");

        // Check faction gold
        assertEquals(60, player.getGold());
        // Check number of training slots
        assertEquals(3, p1.getTrainingSlots());

        // Now, train calvary
        game.trainUnit(p1, calvary);
        
        // Check no. training slots left, and remaining gold
        assertEquals(40, player.getGold());
        assertEquals(2, p1.getTrainingSlots());

        //
        game.trainUnit(p1, calvary);

        assertEquals(20, player.getGold());
        assertEquals(1, p1.getTrainingSlots());

        game.trainUnit(p1, calvary);

        assertEquals(0, player.getGold());
        assertEquals(0, p1.getTrainingSlots());

        game.endTurn();

    }

    @Test
    public void cancelTraining() throws DataInitializationException {
        game = GameController.loadFromSave("src/test/testTraining_troop.json");
        player = game.getCurrentTurn();
        ItemType calvary = ItemType.HEAVY_CAVALRY;
        Province p1 = game.getProvince("P1");
        // TBD
        TrainingSlotEntry one = TrainingSlotEntry somethingTBD;

        // Check for full training slots
        assertEquals(3, p1.getTrainingSlots());

        game.trainUnit(p1, calvary);

        // Check -1 on max training slots
        assertEquals(2, p1.getTrainingSlots());

        game.cancelTraining(one);

        // Check training slot back to full
        assertEquals(3, p1.getTrainingSlots());

        game.endTurn();

    }

    @AfterEach
	public void clear() {

	}
}
