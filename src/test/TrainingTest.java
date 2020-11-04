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

import unsw.engine.*;

public class TrainingTest {
    private GameController game;
    private Faction player;

    /**
     * #US82:
     * AC: This troop should be deployed in the province they were trained in.
     * AC: Troops trained should take <Training time> turns to train.
     * AC: Players should see the gold price for the troop subtracted from their treasury when the command to train is made.
     * 
     * @throws DataInitializationError
     * @throws Exception 
     */
    @Test
    public void basicTraining() throws DataInitializationError {
        game = GameController.loadFromSave("src/test/testTraining_troop.json");
        player = game.getCurrentTurn();
        ItemType cavalry = ItemType.TEST_TROOP;
        Province p1 = game.getProvince("P1");

        
        // Check faction gold
        assertEquals(60, player.getGold());
        // Check number of training slots
        assertEquals(3, p1.getTrainingSlots());

        // Now, train calvary
        game.trainUnit(p1, cavalry);

        // Check no training slots left, and remaining gold
        assertEquals(50, player.getGold());
        assertEquals(2, p1.getTrainingSlots());

        game.trainUnit(p1, cavalry);

        assertEquals(40, player.getGold());
        assertEquals(1, p1.getTrainingSlots());

        game.trainUnit(p1, cavalry);

        assertEquals(30, player.getGold());
        assertEquals(0, p1.getTrainingSlots());
        
        // progress and ensure cavalry unit is trained.
        game.endTurn();
        TestUtil.assertCollectionAttributeEquals(List.of(cavalry, cavalry, cavalry), p1.getUnits(), Unit::getType);
        
        
    }

    /**
     * #US83
     * AC: Upon cancelling, players should see the cancelled unit removed from the list of unit in production.
     * AC: Cancelling should free up a training slot.
     * 
     * @throws DataInitializationError
     */
    @Test
    public void cancelTraining() throws DataInitializationError {
        game = GameController.loadFromSave("src/test/testTraining_troop.json");
        player = game.getCurrentTurn();
        ItemType calvary = ItemType.HEAVY_CAVALRY;
        Province p1 = game.getProvince("P1");

        // Check for full training slots
        assertEquals(3, p1.getTrainingSlots());

        game.trainUnit(p1, calvary);

        // Check -1 on max training slots
        assertEquals(2, p1.getTrainingSlots());

        List<TrainingSlotEntry> copy = new ArrayList<>(p1.getCurrentTraining());
        for (TrainingSlotEntry u : copy) {
            if ( calvary == u.getType() ) {
                game.cancelTraining(u);
            }
        }
        // Check training slot back to full
        assertEquals(3, p1.getTrainingSlots());
        // Check that troop does not train
        game.endTurn();
        assertEquals(0, p1.getUnits().size());

    }

    @AfterEach
	public void clear() {

    }
    
    public static void main(String[] args) throws DataInitializationError {
        GameController game;
        Faction player;
        game = GameController.loadFromSave("src/test/testTraining_troop.json");
        player = game.getCurrentTurn();
    }
}
