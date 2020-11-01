package unsw.gloriaromanus;

import static unsw.gloriaromanus.ActiveType.*;
import static unsw.gloriaromanus.BattleSide.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import util.Concatenator;
import util.MappingIterable;
import util.MathUtil;
import util.Repeat;

// Object is temporary. Do not attempt to store.
public class Battle {
	// Ranged Modifier
	private static final CombatModifier RANGED_MODIFIER = new CombatModifier(CombatModifierMethod._RANGED, null);
	private static final MoraleModifier ATTACKER_LOST_EAGLE = new MoraleModifier(MoraleModifierMethod.LOST_EAGLE, ATTACK);
	private static final MoraleModifier DEFENDER_LOST_EAGLE = new MoraleModifier(MoraleModifierMethod.LOST_EAGLE, DEFEND);
	
	// basic info of a battle
	// Units participating in battle that have not died/routed.
	// Get each army by doing armies.get(ATTACK) or armies.get(DEFEND)
	private Map<BattleSide, List<Unit>> armies = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, List<Unit>> deadUnits = new EnumMap<>(BattleSide.class);
	private Map<BattleSide, List<Unit>> routedUnits = new EnumMap<>(BattleSide.class);
	
	/* number of times an engagement tried to occur.
	 * This distinction is important as there is an edge case if an army is defeated on the 200th
	 * engagement - another engagement will not be attempted, and so it is not a draw.
	 * If the army is not defeated at the 200th engagement, another engagement will be attempted increasing
	 * this number to 201 and resulting in a draw.
	 * See Battle.tryEngagement().
	 */
	
	private ProcessLogger logger = new ProcessLogger("Battle:") ;
	private int numEngagements = 0;
	private Map<BattleSide, Integer> numCasualties = new EnumMap<>(BattleSide.class);
	// record in attackinfo
	private AttackInfo attackInfo;

	private Iterable<MoraleModifier> moraleSupport;
	private Iterable<CombatModifier> combatSupport;
	private Repeat<MoraleModifier> attEagleDebuff;
	private Repeat<MoraleModifier> defEagleDebuff;
	


	// Passed lists should be copies from the province
	public Battle(List<Unit> attackArmy, List<Unit> defendArmy) {
		

		numCasualties.put(ATTACK, 0);
		numCasualties.put(DEFEND, 0);
		armies.put(ATTACK, new ArrayList<>(attackArmy));
		armies.put(DEFEND, new ArrayList<>(defendArmy));
		deadUnits.put(ATTACK, new ArrayList<>());
		deadUnits.put(DEFEND, new ArrayList<>());
		routedUnits.put(ATTACK, new ArrayList<>());
		routedUnits.put(DEFEND, new ArrayList<>());
		
		
		//this.attackInfo = new AttackInfo();
		// Get support modifies from both armies
		// IMPORTANT: Iterables will update as the army and the unit update.
		Iterable<Iterable<CombatModifier>> attCombatSupport = 
				new MappingIterable<>(armies.get(ATTACK), (Unit u) -> u.getCombatModifiers(SUPPORT, ATTACK));
		Iterable<Iterable<CombatModifier>> defCombatSupport = 
				new MappingIterable<>(armies.get(DEFEND), (Unit u) -> u.getCombatModifiers(SUPPORT, DEFEND));
		
		this.combatSupport = new Concatenator<CombatModifier>(attCombatSupport).and(defCombatSupport);

		Iterable<Iterable<MoraleModifier>> attMoraleSupport = 
				new MappingIterable<>(armies.get(ATTACK), (Unit u) -> u.getMoraleModifiers(SUPPORT, ATTACK));
		Iterable<Iterable<MoraleModifier>> defMoraleSupport = 
				new MappingIterable<>(armies.get(DEFEND), (Unit u) -> u.getMoraleModifiers(SUPPORT, DEFEND));
		
		
		// init to zero for now.
		attEagleDebuff = new Repeat<>(ATTACKER_LOST_EAGLE, 0);
		defEagleDebuff = new Repeat<>(DEFENDER_LOST_EAGLE, 0);  
		
		this.moraleSupport = new Concatenator<MoraleModifier>(
					attMoraleSupport)
				.and(attEagleDebuff)
				.and(defMoraleSupport)
				.and(defEagleDebuff);
		
	}
	
	public void setNumEagles(BattleSide side, int val) {
		switch(side) {
		case ATTACK:
			attEagleDebuff.setNum(val);
			break;
		case DEFEND:
			defEagleDebuff.setNum(val);
			break;
		}
	}

	public AttackInfo getResult() {
		try {
			while (!isBattleEnd()) {
				// setupengagement data for unit
	
				// create skirmish
				runSkirmish(pickUnit(ATTACK), pickUnit(DEFEND));
				
				// checkresult and do other stuff
				//
				
			}
		}catch(Exception e) {
			logger.log(e.getMessage());
			for (StackTraceElement ste: e.getStackTrace()) {
				logger.log(ste.toString());
			}
			System.err.println("An error occured. see log for details");
		}finally {
			logger.log("Run complete.");
		}
		logger.log("Attacker:");
		logger.into();
			logger.log("Result:", attackInfo);
			logger.log("Number of Casualties:", getNumCasualties(ATTACK));
			logger.log("Dead Units:", getDeadUnits(ATTACK));
		logger.out();
		
		logger.log("Defender:");
		logger.into();
			logger.log("Result:", attackInfo.defenderView());
			logger.log("Number of Casualties:", getNumCasualties(DEFEND));
			logger.log("Dead Units:", getDeadUnits(DEFEND));
		logger.out();
		
		
		
		return this.attackInfo;
	}
	public void printLog(PrintStream out) {
		out.println(logger);
	}
	public int getNumCasualties(BattleSide side) {
		return numCasualties.get(side);
	}
	public List<Unit> getDeadUnits(BattleSide side) {
		return deadUnits.get(side);
	}
	


	/**
	 * pick a unit from Unit lists
	 * @param side
	 */
	private Unit pickUnit(BattleSide side) {

		// uniformly randomed
		int armyLength = armies.get(side).size();
		return armies.get(side).get(GlobalRandom.nextInt(armyLength)); // RANDOM (ONCE) (LENGTH)
	}
	/**
	 * check if the end of battle condition reached
	 * 
	 * @return if the battle ended
	 */
	private boolean isBattleEnd() {
		if (this.numEngagements > 200) {
			this.attackInfo=AttackInfo.DRAW;
			return true;
		}
		if (armies.get(ATTACK).size()>0){
			if (armies.get(DEFEND).size()>0){
				//both army lives
				return false;
			}
			else{
				//no defender yet attacker exist
				this.attackInfo=AttackInfo.WIN;
				return true;
			}
		}
		if (armies.get(DEFEND).size()>0){
			//no attacker yet defender exist
			this.attackInfo=AttackInfo.LOSE;
			return true;
		}else{
			//no attacker no defender DRAW
			this.attackInfo=AttackInfo.DRAW;
			return true;
		}
	}
	
	// Placeholder
	// IF IMPLEMENTED: Battle Modifier system needs to expose the hasWalls attribute.
	private boolean hasWalls = false;
	public void setWalls(Boolean wall){
		this.hasWalls = wall;
	}
	/**
	 * @pre attackUnit in armies.get(ATTACK), defendUnit in armies.get(DEFEND)
	 * @param attackUnit
	 * @param defendUnit
	 */
	public void runSkirmish(Unit attackUnit, Unit defendUnit) {
		logger.log("Try Skirmish with:");
		logger.into();
			logger.log("Attacker", attackUnit.getType(), attackUnit.getCombatStats());
			logger.log("Defender", defendUnit.getType(), defendUnit.getCombatStats());
		// Begin engagement
		while (tryEngagement()) {
			
			logger.log("Engagement", numEngagements);
			logger.into();
			int attOldHealth = attackUnit.getHealth();
			int defOldHealth = defendUnit.getHealth();

			logger.log("Healths are " + attOldHealth + " : " + defOldHealth);
			Concatenator<CombatModifier> combatModifiers = new Concatenator<>(combatSupport);
			
			// Decide engagement type
			boolean isRanged = tryRanged(attackUnit, defendUnit); // RANDOM (ONCE)
			if(isRanged){
				logger.log("Ranged Engagment");
				combatModifiers = combatModifiers.and(RANGED_MODIFIER);
			}
			
			logger.log("Damage: ");
			logger.log("Attacker", attackUnit.getType(), ":");
			logger.into();
				inflictDamage(attackUnit, defendUnit, ATTACK, combatModifiers, isRanged); // RANDOM 
			logger.out();
			logger.log("Defender", defendUnit.getType(), ":");
			logger.into();
				inflictDamage(defendUnit, attackUnit, DEFEND, combatModifiers, isRanged);
			logger.out();
			// Unit died, end engagement
			// Removal from armies is handled by inflictDamage
			if(!(attackUnit.isAlive() && defendUnit.isAlive())) {
				logger.out();
				logger.log("Skirmish end");
				break;
			}
	

			logger.log("Morale Calculations");
			logger.into();
			
			// Create morale data
			MoraleData data = new MoraleData(attackUnit, defendUnit, armies.get(ATTACK), armies.get(DEFEND));

			// Modify morale
			Concatenator<MoraleModifier> moraleModifiers = new Concatenator<MoraleModifier>(
					moraleSupport,
					defendUnit.getMoraleModifiers(ENGAGEMENT, DEFEND),
					attackUnit.getMoraleModifiers(ENGAGEMENT, ATTACK));
			moraleModifiers.forEach(m->m.modify(data));
			
			StringBuilder modifierString = new StringBuilder();
			for (MoraleModifier m : moraleModifiers) {
				modifierString.append(m.toString());
				modifierString.append(" ");
			}
			logger.log("Final Modifiers:", modifierString);
			
			// Check for breaking
			double attLoss = 1.0 - (double) attackUnit.getHealth() / attOldHealth;
			double defLoss = 1.0 - (double) defendUnit.getHealth() / defOldHealth;
			logger.log(String.format("Losses: %.1f : %.1f", attLoss, defLoss) +
					String.format("\t Morales: %.1f : %.1f", data.getEffectiveMorale(ATTACK), data.getEffectiveMorale(DEFEND)));
			double defLossQuotient;
			double attLossQuotient;
			
			if (attLoss == 0 && defLoss == 0) {
				defLossQuotient = 0.0;
				attLossQuotient = 0.0;
			}else {
				defLossQuotient = defLoss/attLoss;
				attLossQuotient = attLoss/defLoss;
			}
			double attBreakChance = MathUtil.constrain(1.0 + 0.1 * (attLossQuotient - data.getEffectiveMorale(ATTACK)), 0.05, 1);
			double defBreakChance = MathUtil.constrain(1.0 + 0.1 * (defLossQuotient - data.getEffectiveMorale(DEFEND)), 0.05, 1);

			logger.log(String.format("Break chances: %.2f : %.2f", attBreakChance , defBreakChance));
			boolean defBreaks = GlobalRandom.nextUniform() < defBreakChance; // RANDOM (ONCE) 
			boolean attBreaks = GlobalRandom.nextUniform() < attBreakChance; // RANDOM (ONCE)
			
			logger.out();
			if (attBreaks && defBreaks) {
				// end whole skirmish
				logger.out();
				logger.log("Both Break");
				logger.log("Skirmish end");
				route(attackUnit, ATTACK);
				route(defendUnit, DEFEND);
				break;
			} else if (attBreaks) {
				logger.out();
				logger.log("Attacker", attackUnit.getType(), "Breaks");
				runBreaking(defendUnit, attackUnit, DEFEND);	// RANDOM (MANY)
				logger.log("Skirmish end");
				break;
			} else if (defBreaks) {
				logger.out();
				logger.log("Defender", defendUnit.getType(), "Breaks");
				runBreaking(attackUnit, defendUnit, ATTACK);	// RANDOM (MANY)
				logger.log("Skirmish end");
				break;
			}
			logger.out();
		}
		logger.out();
		// Skirmish end
	}
	
	/*
	 * ANYTHING PAST HERE IS USED EXCLUSIVELY WITHIN SKIRMISH()
	 * (Refactor idea: Maybe delagate responsibility to a class?)
	 */
	
	/**
	 * Currently unit death is simply removal from army list and province list. 
	 * That should be all we need to do for our current design
	 */

	
	/**
	 * Runs breaking process
	 */
	public void runBreaking(Unit chaseUnit, Unit routeUnit, BattleSide chaseSide) {

		// Try engagement
		while(tryEngagement()) {
			logger.log("Break Engagement", numEngagements);
			logger.into();
			boolean isRanged = tryRanged(chaseUnit, routeUnit); // RANDOM (ONCE)
			
			Concatenator<CombatModifier> combatModifiers = new Concatenator<>(combatSupport);
			if(isRanged){
				combatModifiers = combatModifiers.and(RANGED_MODIFIER);
				logger.log("Ranged Engagement");
			}
			logger.into();
			inflictDamage(chaseUnit, routeUnit, chaseSide, combatModifiers, isRanged);
			logger.out();
			if(!routeUnit.isAlive()) {
				// Unit death handled by inflict damage
				logger.out();
				return;
			}
			
			double routeChance = MathUtil.max(0.5 + 0.1 * (routeUnit.getSpeed() - chaseUnit.getSpeed()), 0.1);
			logger.out();
			
			// unit routes
			if(GlobalRandom.nextUniform() < routeChance) { // RANDOM (ONCE)
				// remove unit from army.
				route(routeUnit, chaseSide.other());
				logger.log("Victim Escapes");
				// TODO log route? ??? or dont
				return;
			}
		}
	}
	/**
	 * Handles picking units in elephants amok, CombatModifiers, damage, and killing units 
	 */
	public void inflictDamage(Unit aggressor, Unit victim, BattleSide aggressorSide, Concatenator<CombatModifier> combatModifiers, boolean isRanged) {
    	// Elephants running amok:
		// Late nights and a dim screen are all I've known,
		// Every new day bringing new incorrect solution.
		// Perhaps I should have ignored you,
		// Hoping the tutors count infrastructure instead.
		// Ask me why, Ill rant about damage and morale 
		// Not to mention uprooting the old system
		// And am I a better person for it?
		// The worst part is I may never know.

		if(aggressor.getType() == ItemType.ELEPHANTS && GlobalRandom.nextUniform() < 0.1 && armies.get(aggressorSide).size() != 1){ // RANDOM (ONCE)
    		// Ensuring that dumb elephant does not attack itself
			// (As much as I would like it to)
			// This is fine right?
			do {
    			victim = pickUnit(aggressorSide);
    		}while(Objects.equals(victim,aggressor));

			logger.log("Elephants amok with " + victim.getType());
    	}
		// Create CombatData
		CombatData data;
		switch(aggressorSide) {
		case ATTACK:
			data = new CombatData(aggressor, victim, armies.get(ATTACK), armies.get(DEFEND), isRanged, hasWalls);
			break;
		case DEFEND:
			data = new CombatData(victim, aggressor, armies.get(ATTACK), armies.get(DEFEND), isRanged, hasWalls);
			break;
		default:
			throw new NullPointerException("aggressorSide was null you dumbass");
		}
		
		// Get combat modifiers for both troops and apply them to data
		combatModifiers = combatModifiers
			.and(aggressor.getCombatModifiers(ENGAGEMENT, aggressorSide))
			.and(victim.getCombatModifiers(ENGAGEMENT, aggressorSide.other()));
		StringBuilder modifierString = new StringBuilder();
		for (CombatModifier m : combatModifiers) {
			modifierString.append(m.toString());
			modifierString.append(" ");
		}
		logger.log("Final Modifiers: ", modifierString);
		
		
		combatModifiers.forEach((m) -> m.modify(data));
		
		// Caculate inflicted casualties
		double effectiveArmour = data.getEffectiveArmour(aggressorSide.other());
		
		logger.log("Armour", effectiveArmour, "Attack:", data.getAttack(aggressorSide));
		
		
		double beserkerIgnoreRangedUnitDamageAndUseThisDamageNumberInsteadAlsoCanYouTellThatImAnnoyed = 1.0;
		double damage;
		if(effectiveArmour != 0) {
			damage = 0.1 * data.getAttack(aggressorSide)/effectiveArmour;
		}else {
			damage = beserkerIgnoreRangedUnitDamageAndUseThisDamageNumberInsteadAlsoCanYouTellThatImAnnoyed;
		}
		double multiplier = MathUtil.max(0, GlobalRandom.nextGaussian() + 1);	
		logger.log("Multiplier:", multiplier);
		
		int casualties = (int)Math.round(MathUtil.max(0, multiplier * damage * victim.getHealth())); // RANDOM (ONCE) (GAUSSIAN)
		
		int oldHealth = victim.getHealth();
		
		victim.damage(casualties);
		addCasualties(aggressorSide.other(), oldHealth-victim.getHealth());
		logger.log("Casualties: ", oldHealth + "->" +  victim.getHealth());
		
		if(!victim.isAlive()) {
			kill(victim, aggressorSide.other());
		}
	}
	
	/**
	 * Decide whether we have exceeded the engagement quota
	 * @return
	 */
	private boolean tryEngagement() {
		boolean out = numEngagements < 200;
		// Increment num attempted engagements
		numEngagements ++;
		return out;
	}
	private void addCasualties(BattleSide side, int num) {
		numCasualties.put(side, numCasualties.get(side) + num);		
	}
	private void kill(Unit u, BattleSide side) {
		logger.log("Victim", u.getType(), "dies");
		deadUnits.get(side).add(u);
		armies.get(side).remove(u);
	}
	private void route(Unit u, BattleSide side) {
		logger.log(u.getType(), "Routes");
		routedUnits.get(side).add(u);
		armies.get(side).remove(u);
	}
	/**
	 * Uses the given parameters (and RNG) to decide whether an engagement is ranged, and 
	 * @returns returns the input and with a ranged modifier if the engagment is ranged. 
	 * returns the input CombatModifier Concatenator otherwise.
	 */
	public boolean tryRanged(Unit u1, Unit u2) {
		if (u1.isRanged() == u2.isRanged()) {
			return u1.isRanged();
		}
		
		// Not in spec anymore, but whatever man
//		if (u1.getUnitClass() == UnitClass.TOWER || u2.getUnitClass() == UnitClass.TOWER ) {
//			return true;
//		}
		
		double baseChance = hasWalls? 0.9 : 0.5;
		
		// Based on speed
		// We know either u1 or u2 is ranged.
		double rangedChance = baseChance + (u1.getSpeed() - u2.getSpeed()) * (u1.isRanged() ? 0.1 : -0.1);
		rangedChance = MathUtil.constrain(rangedChance, 0.05, 0.95);
		return GlobalRandom.nextUniform() < rangedChance; // RANDOM (ONCE)
	}
	

}

class ProcessLogger{
	private StringBuilder log;
	private int depth = 0;
	ProcessLogger(String s){
		log = new StringBuilder(s);
		log.append("\n\n");
	}
	public void into() {
		depth ++;
		
	}
	public void out() {
		depth --;	
	}
	
	
	
	public <T> T log(T val) {
		log.append("\t".repeat(depth));
		log.append(val);
		log.append("\n");
		return val;
	}
	
	public <T> T log(String name, T val) {
		log.append("\t".repeat(depth));
		log.append(name);
		log.append(" ");
		log.append(val);
		log.append("\n");
		return val;
	}
	public <T> void log(Object ... vals) {
		log.append("\t".repeat(depth));	
		for (Object val : vals) {
			log.append(val);
			log.append(" ");
		}
		log.append("\n");
	}
	
	public int log(String name, int val) {
		log.append("\t".repeat(depth));
		log.append(name);
		log.append(" ");
		log.append(val);		
		log.append("\n");
		return val;
	}
	
	public double log(String name, double val) {
		log.append("\t".repeat(depth));
		log.append(name);
		log.append(" ");	
		log.append(val);		
		log.append("\n");
		return val;
	}
	
	@Override
	public String toString() {
		return log.toString();
	}
	
}
