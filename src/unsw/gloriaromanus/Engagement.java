package unsw.gloriaromanus;

import java.util.List;
import util.Concatenator;
public class Engagement {
    //Parameters
    CombatData attacker;
    CombatData defender;
    public Engagement(CombatData attacker, CombatData defender){
        this.attacker = attacker;
        this.defender = defender;
    }
    public boolean result(){
        //calculate the result of a engagement
        //TODO
        return false;
    }
    
    // Data required:
    List<Unit> attackArmy;
    List<Unit> defendArmy;
    Unit attackUnit;
    Unit defendUnit;
    int numEngagements = 0;
    
    /**
     * Info in
     * 
     * 
     */
    public void skirmish() {
    	
    	// Get support modifiers
    	Concatenator<CombatModifier> combatSupportModifiers;
    	Concatenator<MoraleModifier> moraleSupportModifiers;
    	
    	// Begin engagement
    	while(attackUnit.isAlive() && defendUnit.isAlive()) {
    		MoraleData d = new MoraleData(attackUnit, defendUnit , attackArmy, defendArmy);
    	
    	}
    	
    }
    public void engagement() {
    	
    	
    	
    }
    
}
