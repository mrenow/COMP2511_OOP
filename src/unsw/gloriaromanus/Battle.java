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
import util.Repeat;

// Object is temporary. Do not attempt to store.
class Battle {
	// Ranged Modifier
	private static final CombatModifier RANGED_MODIFIER = new CombatModifier(CombatModifierMethod.RANGED, null);
	private static final MoraleModifier ATTACKER_LOST_EAGLE = new MoraleModifier(MoraleModifierMethod.LOST_EAGLE, ATTACK);
	private static final MoraleModifier DEFENDER_LOST_EAGLE = new MoraleModifier(MoraleModifierMethod.LOST_EAGLE, DEFEND);
	
	// basic info of a battle
	// Units participating in battle that have not died/routed.
	// Get each army by doing armies.get(ATTACK) or armies.get(DEFEND)
	private EnumMap<BattleSide, List<Unit>> armies = new EnumMap<>(BattleSide.class);
	/* number of times an engagement tried to occur.
	 * This distinction is important as there is an edge case if an army is defeated on the 200th
	 * engagement - another engagement will not be attempted, and so it is not a draw.
	 * If the army is not defeated at the 200th engagement, another engagement will be attempted increasing
	 * this number to 201 and resulting in a draw.
	 * See Battle.tryEngagement().
	 */
	private int numEngagements = 0;
	// record in attackinfo
	private AttackInfo attackInfo;

	// data for setup a engagement
	private Unit attackUnit;
	private Unit defendUnit;
	
	private Faction attackFaction;
	private Faction defendFaction;
	private Province attackProvince;
	private Province defendProvince;
	
	
	private CombatData aData;
	private CombatData dData;
	// current engagement
	private Engagement engagement;
	private Iterable<MoraleModifier> moraleSupport;
	private Iterable<CombatModifier> combatSupport;


	// Passed lists should be copies from the province
	Battle(List<Unit> attackArmy, List<Unit> defendArmy) {
		armies.put(ATTACK, attackArmy);
		armies.put(DEFEND, defendArmy);
		
		attackProvince = attackArmy.get(0).getProvince();
		attackFaction = attackProvince.getOwner();
		defendProvince = defendArmy.get(0).getProvince();
		defendFaction = defendProvince.getOwner();
		

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
		
		// Lost eagles will be constant
		Iterable<MoraleModifier> attEagleDebuff =
				new Repeat<>(ATTACKER_LOST_EAGLE, attackFaction.getNumLostEagles());
		Iterable<MoraleModifier> defEagleDebuff =
				new Repeat<>(DEFENDER_LOST_EAGLE, defendFaction.getNumLostEagles());  
		
		this.moraleSupport = new Concatenator<MoraleModifier>(
					attMoraleSupport)
				.and(attEagleDebuff)
				.and(defMoraleSupport)
				.and(defEagleDebuff);
		
	}

	public AttackInfo getResult() {
	
		int defNumEagles = 0;
		for (Unit u  : armies.get(DEFEND)) {
			if(u.getType() == ItemType.ROMAN_LEIGIONARY) {
				defNumEagles++;
			}
		}
	
		while (!isBattleEnd()) {
			// setupengagement data for unit
			setUp();
			
			
			// create skirmish
			runSkirmish(attackUnit, defendUnit);
			
			// checkresult and do other stuff
			//
			
		}
		// default: attacker never wins
		if (this.numEngagements >= 200) {
			this.attackInfo = AttackInfo.DRAW;
		}
		
		// After attackinfo is assigned
		// if the current province is taken
		if(this.attackInfo == AttackInfo.WIN) {
			defendFaction.putLostEagles(defendProvince, defNumEagles);
		}
		
		return this.attackInfo;
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
		if (this.numEngagements > 200) {
			return true;
		}
		return true;
	}
	
	// Placeholder
	private boolean hasWalls = false;
	public void setWalls(Boolean wall){
		this.hasWalls = wall;
	}
	public void runSkirmish(Unit attackUnit, Unit defendUnit) {
		// Begin engagement
		while (tryEngagement()) {
			
			int attOldHealth = attackUnit.getHealth();
			int defOldHealth = defendUnit.getHealth();
			
			Concatenator<CombatModifier> combatModifiers = new Concatenator<>(combatSupport);

			boolean isRanged = tryRanged(attackUnit, defendUnit);
			if(isRanged){
				combatModifiers = combatModifiers.and(RANGED_MODIFIER);
			}
			
			inflictDamage(attackUnit, defendUnit, ATTACK, combatModifiers, isRanged);
			inflictDamage(defendUnit, attackUnit, DEFEND, combatModifiers, isRanged);
			
			// Unit died, end engagement
			// Removal from armies is handled by inflictDamage
			if(!(attackUnit.isAlive() && defendUnit.isAlive())) {
				break;
			}
				
			// Create morale data
			MoraleData data = new MoraleData(attackUnit, defendUnit, armies.get(ATTACK), armies.get(DEFEND));

			// Modify morale
			Concatenator<MoraleModifier> moraleModifiers = new Concatenator<MoraleModifier>(
					moraleSupport,
					defendUnit.getMoraleModifiers(ENGAGEMENT, DEFEND),
					attackUnit.getMoraleModifiers(ENGAGEMENT, ATTACK));
			moraleModifiers.forEach(m->m.modify(data));
			
			
			// Check for breaking
			double attLoss = 1.0 - (double) attackUnit.getHealth() / attOldHealth;
			double defLoss = 1.0 - (double) defendUnit.getHealth() / defOldHealth;
			double attBreakChance = MathUtil.constrain(1.0 - 0.1 * (data.getEffectiveMorale(ATTACK) + attLoss / defLoss), 0.05, 1);
			double defBreakChance = MathUtil.constrain(1.0 - 0.1 * (data.getEffectiveMorale(DEFEND) + defLoss / attLoss), 0.05, 1);

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

		// Try engagement
		while(tryEngagement()) {
			boolean isRanged = tryRanged(chaseUnit, routeUnit);
			if(isRanged){
				combatModifiers = combatModifiers.and(RANGED_MODIFIER);
			}
			
			inflictDamage(chaseUnit, routeUnit, chaseSide, combatModifiers, isRanged);
			if(!routeUnit.isAlive()) {
				return;
			}
			
			double routeChance = MathUtil.max(0.5 + 0.1 * (routeUnit.getSpeed() - chaseUnit.getSpeed()), 0.1);
			// unit routes
			if(GlobalRandom.nextUniform() < routeChance) {
				// remove unit from army.
				armies.get(chaseSide.other()).remove(routeUnit);
				// TODO log route? ??? or dont
				return;
			}
		}
	}
	/**
	 * Handles picking units in elephants amok, CombatModifiers, damage, and killing units 
	 */
	private void inflictDamage(Unit aggressor, Unit victim, BattleSide aggressorSide, Concatenator<CombatModifier> combatModifiers, boolean isRanged) {
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
			data = new CombatData(aggressor, victim, armies.get(ATTACK), armies.get(DEFEND), isRanged);
			break;
		case DEFEND:
			data = new CombatData(victim, aggressor, armies.get(ATTACK), armies.get(DEFEND), isRanged);
			break;
		default:
			throw new NullPointerException("aggressorSide was null you dumbass");
		}
		
		// Get combat modifiers for both troops and apply them to data
		combatModifiers = combatModifiers
			.and(aggressor.getCombatModifiers(ENGAGEMENT, aggressorSide))
			.and(victim.getCombatModifiers(ENGAGEMENT, aggressorSide.other()));
		
		combatModifiers.forEach((m) -> m.modify(data));
		
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
	
	/**
	 * Uses the given parameters (and RNG) to decide whether an engagement is ranged, and 
	 * @returns returns the input and with a ranged modifier if the engagment is ranged. 
	 * returns the input CombatModifier Concatenator otherwise.
	 */
	private boolean tryRanged(Unit u1, Unit u2) {
		if (u1.isRanged() && u2.isRanged()) {
			return u1.isRanged();
		}
		// Not in spec anymore
		if (u1 instanceof Tower || u2 instanceof Tower) {
			return true;
		}
		
		double baseChance = hasWalls? 0.9 : 0.5;
		
		// Based on speed
		// We know either u1 or u2 is ranged.
		double rangedChance = baseChance + (u1.getSpeed() - u2.getSpeed()) * (u1.isRanged() ? 0.1 : -0.1);
		rangedChance = MathUtil.constrain(rangedChance, 0.05, 0.95);
		return GlobalRandom.nextUniform() < rangedChance;
	}


}
