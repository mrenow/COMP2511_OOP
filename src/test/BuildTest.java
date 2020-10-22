package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import unsw.gloriaromanus.*;



public class BuildTest {
	public class UnitTest{
	    @Before
	    public void setupGame() {
	    	
	    }
	    
		
		@Test
	    public void blahTest(){
	        assertEquals("a", "b");
	        String s;
	    }
	    
	    @Test
	    public void blahTest2(){
	        Unit u = new Unit();
	        assertEquals(u.getNumTroops(), 50);
	    }
	}
}
