package unsw.gloriaromanus;

import static unsw.gloriaromanus.ActiveType.*;
import static unsw.gloriaromanus.BattleSide.*;

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
	List<Unit> attackArmy;
	List<Unit> defendArmy;
	int numEngage = 0;

	// data for setup a engagement
	Unit attackUnit;
	Unit defendUnit;
	CombatData aData;
	CombatData dData;
	// current engagement
	Engagement engagement;
	Iterable<MoraleModifier> moraleSupport;
	Iterable<CombatModifier> combatSupport;
	private int numEngagements;

	public Battle() {
	}

	public Battle(List<Unit> attackArmy, List<Unit> defendArmy) {
		this.attackArmy = attackArmy;
		this.defendArmy = defendArmy;

		// Get support modifies from both armies
		// IMPORTANT: Iterables will update as the army updates.
		this.combatSupport = new Concatenator<CombatModifier>(new MappingIterable<Unit, Iterable<CombatModifier>>(
				this.attackArmy, u -> u.getCombatModifiers(SUPPORT, ATTACKER)))
						.and(new MappingIterable<Unit, Iterable<CombatModifier>>(this.defendArmy,
								u -> u.getCombatModifiers(SUPPORT, DEFENDER)));

		this.moraleSupport = new Concatenator<MoraleModifier>(new MappingIterable<Unit, Iterable<MoraleModifier>>(
				this.attackArmy, u -> u.getMoraleModifiers(SUPPORT, ATTACKER)))
						.and(new MappingIterable<Unit, Iterable<MoraleModifier>>(this.defendArmy,
								u -> u.getMoraleModifiers(SUPPORT, DEFENDER)));
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
			this.numEngage++;
		}
		// default: attacker never wins
		return false;
	}

	private void setUp() {
		// read in buffs
		// TODO
		// random choose two units
		this.attackUnit = pickUnit(ATTACKER);
		this.defendUnit = pickUnit(DEFENDER);
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
		switch (side) {
		case ATTACKER:
			return attackArmy.get(GlobalRandom.nextInt(attackArmy.size()));
		case DEFENDER:
			return defendArmy.get(GlobalRandom.nextInt(defendArmy.size()));
		default:
			// only when side is null
			return null;
		}
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

		Unit routeUnit = null;
		Unit chaseUnit = null;

		while (numEngagements < 200) {
			numEngagements++;

			int attOldHealth = attackUnit.getHealth();
			int defOldHealth = defendUnit.getHealth();
			
			Concatenator<CombatModifier> combatModifiers = new Concatenator<>(combatSupport);

			combatModifiers = tryModifyRanged(combatModifiers, attackUnit, defendUnit, hasWalls)

			inflictDamage(attackUnit, defendUnit, ATTACKER, combatModifiers);
			inflictDamage(defendUnit, attackUnit, DEFENDER, combatModifiers);
			
			// Unit died, end engagement
			if(!(attackUnit.isAlive() && defendUnit.isAlive())) {
				break;
			}
				
			// Create morale data
			MoraleData d = new MoraleData(attackUnit, defendUnit, attackArmy, defendArmy);

			// Modify morale
			new Concatenator<MoraleModifier>(moraleSupport, defendUnit.getMoraleModifiers(ENGAGEMENT, DEFENDER),
					attackUnit.getMoraleModifiers(ENGAGEMENT, ATTACKER)).forEach((m) -> m.modify(d));


			
			// Check for breaking
			double attLoss = 1.0 - (double) attackUnit.getHealth() / attOldHealth;
			double defLoss = 1.0 - (double) defendUnit.getHealth() / defOldHealth;
			double attBreakChance = MathUtil.constrain(1 - 0.1 * (d.getMorale(ATTACKER) + attLoss / defLoss), 0.05, 1);
			double defBreakChance = MathUtil.constrain(1 - 0.1 * (d.getMorale(DEFENDER) + defLoss / attLoss), 0.05, 1);

			boolean defBreaks = GlobalRandom.nextUniform() < defBreakChance;
			boolean attBreaks = GlobalRandom.nextUniform() < attBreakChance;

			if (attBreaks && defBreaks) {
				// end whole engagement
				break;
			} else if (attBreaks) {
				runRoute(defendUnit, attackUnit, DEFENDER);
				break;
			} else if (defBreaks) {
				runRoute(attackUnit, defendUnit, ATTACKER);
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
		case ATTACKER:
			data = new CombatData(aggressor, victim, attackArmy, defendArmy);
			break;
		case DEFENDER:
			data = new CombatData(victim, aggressor, attackArmy, defendArmy);
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
		boolean victimRemoved = attackArmy.remove(u) || defendArmy.remove(u);
		assert victimRemoved : "Victim wasnt in any army?????";
		// TODO log casualty
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
