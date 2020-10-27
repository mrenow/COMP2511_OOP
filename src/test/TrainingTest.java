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
     * @throws Exception
     */
    @BeforeEach
    public void setupGame() throws Exception{
        // TODO
    	game = GameController.loadFromSave("");
    	player = game.getCurrentTurn(); 
    }

    @Test
    public void basicTraining() {

    }

    @Test
    public void trainingWithGold() {

    }

    @Test
    public void trainingWithSlots() {

    }

    @Test
    public void cancelTraining() {
        
    }

    @AfterEach
	public void clear() {

	}
}
