package unsw.gloriaromanus;

import static unsw.gloriaromanus.BattleSide.*;
import static unsw.gloriaromanus.ActiveType.*;
import java.util.List;
import util.Concatenator;
import util.MappingIterable;
import util.MathUtil;

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
    public void skirmish(Iterable<MoraleModifier> moraleSupport, Iterable<CombatModifier> combatSupport) {
    	
    	// Get support modifiers
    	// Pog factor: automatically updates with attackArmy
    	/*
    	Iterable<CombatModifier> combatSupport = new Concatenator<CombatModifier>(
    				 new MappingIterable<Unit,Iterator<CombatModifier>>(attackArmy, unit -> unit.getCombatModifiers(SUPPORT, ATTACKER)))
    			.and(new MappingIterable<Unit,Iterator<CombatModifier>>(defendArmy, unit -> unit.getCombatModifiers(SUPPORT, DEFENDER)));
    			
    	Iterable<MoraleModifier> moraleSupport = new Concatenator<MoraleModifier>(
    				 new MappingIterable<Unit,Iterator<MoraleModifier>>(attackArmy, unit -> unit.getMoraleModifiers(SUPPORT, ATTACKER)))
    			.and(new MappingIterable<Unit,Iterator<MoraleModifier>>(defendArmy, unit -> unit.getMoraleModifiers(SUPPORT, DEFENDER)));   	
    	*/
    	
    	
    	// Begin engagement
    	int attOldHealth = attackUnit.getHealth();
    	int defOldHealth = defendUnit.getHealth();
    	Unit routeUnit = null;
    	Unit chaseUnit = null;
    	
    	
    	while(attackUnit.isAlive() && defendUnit.isAlive()) {
    		// Create morale data
    		MoraleData d = new MoraleData(attackUnit, defendUnit , attackArmy, defendArmy);
    		
    		// Modify morale
    		new Concatenator<MoraleModifier>(moraleSupport,
    				defendUnit.getMoraleModifiers(ENGAGEMENT, DEFENDER),
    				attackUnit.getMoraleModifiers(ENGAGEMENT, ATTACKER)).forEach((m) -> m.modify(d));
    		
    		// Check for breaking
    		double attLoss= 1.0 - (double)attackUnit.getHealth()/attOldHealth;   
    		double defLoss= 1.0 - (double)defendUnit.getHealth()/defOldHealth;   
    		double attBreakChance = MathUtil.constrain(1 - 0.1*(d.getMorale(ATTACKER) + attLoss/defLoss), 0.05, 1);
    		double defBreakChance = MathUtil.constrain(1 - 0.1*(d.getMorale(DEFENDER) + defLoss/attLoss), 0.05, 1);
    		
    		if(GlobalRandom.nextUniform() < defBreakChance) {
    			routeUnit = defendUnit;
    			chaseUnit = attackUnit;
    		}
    		if(GlobalRandom.nextUniform() < attBreakChance) {
       			routeUnit = attackUnit;
    			chaseUnit = defendUnit;   			
    		}
    		
    		Concatenator<CombatModifier> combatModifiers;
    		
    		
    		if () 
    		
    		
    		//
    		
    	}
    	
    }
    
    private boolean tryRanged(Unit u1, Unit u2, boolean walls) {
    	if(u1.isRanged() == u2.isRanged()) {
    		return u1.isRanged();
    	}
    	if(u1 instanceof Tower || u2 instanceof Tower) {
    		return true;
    	}
    	double baseChance = 0.5;
    	if(walls) {
    		baseChance = 0.9;
    	}
    	// Based on speed
    	
    	double rangedChance = baseChance + (u1.getSpeed() - u2.getSpeed()) * (u1.isRanged() ? 0.1 : -0.1);
    	rangedChance = MathUtil.constrain(rangedChance, 0.1, min)
    	return GlobalRandom.nextUniform() < rangedChance;    	
    }
    public void engagement() {
    	
    	
    	
    }
    
}
