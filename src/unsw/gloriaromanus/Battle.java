package unsw.gloriaromanus;

import static unsw.gloriaromanus.ActiveType.*;
import static unsw.gloriaromanus.BattleSide.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import util.Concatenator;
import util.MappingIterable;
import util.MathUtil;

public class Battle {
	// Ranged Modifier
	static final CombatModifier RANGED_MODIFIER = new CombatModifier(CombatModifierMethod.RANGED, null);
	
	
	// basic info of a battle
	// Units participating in battle that have not died/routed.
	// Get each army by doing armies.get(ATTACK) or armies.get(DEFEND)
	private EnumMap<BattleSide, List<Unit>> armies = new EnumMap<>(BattleSide.class);
	private int numEngagements;

	// data for setup a engagement
	Unit attackUnit;
	Unit defendUnit;
	CombatData aData;
	CombatData dData;
	// current engagement
	Engagement engagement;
	Iterable<MoraleModifier> moraleSupport;
	Iterable<CombatModifier> combatSupport;

	public Battle() {
	}

	// Passed lists should be copies from the province
	public Battle(List<Unit> attackArmy, List<Unit> defendArmy) {
		armies.put(ATTACK, attackArmy);
		armies.put(DEFEND, defendArmy);

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
				
		this.moraleSupport = new Concatenator<MoraleModifier>(attMoraleSupport).and(defMoraleSupport);
		
	}

	public boolean getResult() {

		while (!isBattleEnd()) {
			// setupengagement data for unit
			setUp();
			// create engagement
			Engagement engagement = new Engagement(aData, dData);
			// checkresult and do other stuff
			engagement.result();
			// flee route breaking unitdead stuff...
			// Already done in skirmish
			//this.numEngage++;
		}
		// default: attacker never wins
		
		return false;
	}

	private void setUp() {
		// read in buffs
		// TODO
		// random choose two units
		this.attackUnit = pickUnit(ATTACK);
		this.defendUnit = pickUnit(DEFEND);
//        this.aData = new CombatData();
//        this.dData = new CombatData();
		// setupdata

		// do some change to data
	}

	/**
	 * pick a unit from Unit lists
	 * 
	 * @param units
	 * @return unit choosed from list
	 */
	private Unit pickUnit(BattleSide side) {

		// uniformly randomed
		int armyLength = armies.get(side).size();
		return armies.get(side).get(GlobalRandom.nextInt(armyLength));
	}
	/**
	 * check if the end of battle condition reached
	 * 
	 * @return if the battle ended
	 */
	private boolean isBattleEnd() {
		// TODO for now battle never start
		return true;
	}
	
	// Placeholder
	private boolean hasWalls = false;
	public void runSkirmish(Unit attackUnit, Unit defendUnit) {
		// Begin engagement
		
		while (numEngagements < 200) {
			numEngagements++;

			int attOldHealth = attackUnit.getHealth();
			int defOldHealth = defendUnit.getHealth();
			
			Concatenator<CombatModifier> combatModifiers = new Concatenator<>(combatSupport);

			combatModifiers = tryModifyRanged(combatModifiers, attackUnit, defendUnit, hasWalls);

			inflictDamage(attackUnit, defendUnit, ATTACK, combatModifiers);
			inflictDamage(defendUnit, attackUnit, DEFEND, combatModifiers);
			
			// Unit died, end engagement
			// Removal from armies is handled by inflictDamage
			if(!(attackUnit.isAlive() && defendUnit.isAlive())) {
				break;
			}
				
			// Create morale data
			MoraleData d = new MoraleData(attackUnit, defendUnit, armies.get(ATTACK), armies.get(DEFEND));

			// Modify morale
			new Concatenator<MoraleModifier>(moraleSupport, defendUnit.getMoraleModifiers(ENGAGEMENT, DEFEND),
					attackUnit.getMoraleModifiers(ENGAGEMENT, ATTACK)).forEach((m) -> m.modify(d));
			
			// Check for breaking
			double attLoss = 1.0 - (double) attackUnit.getHealth() / attOldHealth;
			double defLoss = 1.0 - (double) defendUnit.getHealth() / defOldHealth;
			double attBreakChance = MathUtil.constrain(1 - 0.1 * (d.getMorale(ATTACK) + attLoss / defLoss), 0.05, 1);
			double defBreakChance = MathUtil.constrain(1 - 0.1 * (d.getMorale(DEFEND) + defLoss / attLoss), 0.05, 1);

			boolean defBreaks = GlobalRandom.nextUniform() < defBreakChance;
			boolean attBreaks = GlobalRandom.nextUniform() < attBreakChance;

			if (attBreaks && defBreaks) {
				// end whole skirmish
				break;
			} else if (attBreaks) {
				runBreaking(defendUnit, attackUnit, DEFEND);
				break;
			} else if (defBreaks) {
				runBreaking(attackUnit, defendUnit, ATTACK);
				break;
			}
		}
		// Skirmish end
	}
	
	/*
	 * ANYTHING PAST HERE IS USED EXCLUSIVELY WITHIN SKIRMISH()
	 * (Refactor idea: Maybe delagate responsibility to a class?)
	 */
	
	/**
	 * Currently unit death is simply removal from army list. 
	 * That should be all we need to do for our current design
	 */

	
	/**
	 * Runs breaking process
	 */
	private void runBreaking(Unit chaseUnit, Unit routeUnit, BattleSide chaseSide) {
		Concatenator<CombatModifier> combatModifiers = new Concatenator<>(combatSupport);
		while(routeUnit.isAlive()) {
			inflictDamage(chaseUnit, routeUnit, chaseSide, tryModifyRanged(combatModifiers, attackUnit, defendUnit, hasWalls));
			
			double routeChance = 0.5 + 0.1 * (routeUnit.getSpeed() - chaseUnit.getSpeed());
			if(GlobalRandom.nextUniform() < routeChance) {
				armies.get(chaseSide.other()).remove(routeUnit);
				// TODO log route? ??? or dont
				return;
			}
		}
	}
	/**
	 * Handles picking units in elephants amok, CombatModifiers, damage, and killing units 
	 */
	private void inflictDamage(Unit aggressor, Unit victim, BattleSide aggressorSide, Concatenator<CombatModifier> combatModifiers) {
    	// Elephants running amok:
		// Late nights and a dim screen are all I've known,
		// Every new day bringing new incorrect solution.
		// Perhaps I should have ignored you,
		// Hoping the tutors count infrastructure instead.
		// Ask me why, Ill rant about damage and morale 
		// Not to mention uprooting the old system
		// And am I a better person for it?
		// The worst part is I may never know.

		if(aggressor.getType() == ItemType.ELEPHANTS && GlobalRandom.nextUniform() < 0.1){
    		// Ensuring that dumb elephant does not attack itself
			// (As much as I would like it to)
			// This is fine right?
			do {
    			victim = pickUnit(aggressorSide);
    		}while(!Objects.equals(victim,aggressor));
    	}
		// Create CombatData
		CombatData data;
		switch(aggressorSide) {
		case ATTACK:
			data = new CombatData(aggressor, victim, armies.get(ATTACK), armies.get(DEFEND));
			break;
		case DEFEND:
			data = new CombatData(victim, aggressor, armies.get(ATTACK), armies.get(DEFEND));
			break;
		default:
			throw new NullPointerException("aggressorSide was null you dumbass");
		}
		
		// Get combat modifiers for both troops and apply them to data
		combatModifiers
			.and(aggressor.getCombatModifiers(ENGAGEMENT, aggressorSide))
			.and(victim.getCombatModifiers(ENGAGEMENT, aggressorSide.other()))
			.forEach(m-> m.modify(data));
		
		// Caculate inflicted casualties
		double effectiveArmour = data.getEffectiveArmour(aggressorSide.other());
		if(effectiveArmour == 0) {
			int beserkerIgnoreRangedUnitDamageAndUseThisDamageNumberInsteadAlsoCanYouTellThatImAnnoyed = 10;
			victim.damage(beserkerIgnoreRangedUnitDamageAndUseThisDamageNumberInsteadAlsoCanYouTellThatImAnnoyed);
		}else {
			double casualties = GlobalRandom.nextGaussian()*0.1*data.getAttack(aggressorSide)/effectiveArmour;
			victim.damage((int)Math.round(casualties));
		}
		
		// Kill unit by yeeting it out of the list
		if(!victim.isAlive()) {
			killUnit(victim);
		}
	}
	private void killUnit(Unit u) {
		u.kill();
		// Remove from armies participaing in battle
		armies.get(ATTACK).remove(u);
		armies.get(DEFEND).remove(u);
		// TODO log casualty

	}
	private void routeUnit(Unit u){
		armies.get(ATTACK).remove(u);
		armies.get(DEFEND).remove(u);
	}
	
	/**
	 * Uses the given parameters (and RNG) to decide whether an engagement is ranged, and 
	 * @returns returns the input and with a ranged modifier if the engagment is ranged. 
	 * returns the input CombatModifier Concatenator otherwise.
	 */
	private static Concatenator<CombatModifier> tryModifyRanged(Concatenator<CombatModifier> m, Unit u1, Unit u2, boolean walls) {
		if (u1.isRanged() && u2.isRanged()) {
			return m.and(RANGED_MODIFIER);
		}else if (!u1.isRanged() && u2.isRanged()) {
			return m;
		}
		// Not in spec anymore
		if (u1 instanceof Tower || u2 instanceof Tower) {
			return m.and(RANGED_MODIFIER);
		}
		
		double baseChance = 0.5;
		if (walls) {
			baseChance = 0.9;
		}
		
		// Based on speed
		// We know either u1 or u2 is ranged.
		double rangedChance = baseChance + (u1.getSpeed() - u2.getSpeed()) * (u1.isRanged() ? 0.1 : -0.1);
		rangedChance = MathUtil.constrain(rangedChance, 0.05, 0.95);
		if(GlobalRandom.nextUniform() < rangedChance) {
			return m.and(RANGED_MODIFIER);
		} else {
			return m;
		}
	}


}
