package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BattleCharacteristic {
	
	double armour = 0;
	double morale = 0;
	double speed = 0;
	double attack = 0;
	double defenseSkill = 0;
	double shieldDefense = 0;
	
	// These should not change 
	double armourMult = 1;
	double moraleMult = 1;
	double speedMult = 1;
	double attackMult = 1;
	
	@JsonCreator
	public BattleCharacteristic(
			@JsonProperty("armour") double armour,
			@JsonProperty("morale") double morale,
			@JsonProperty("speed") double speed,
			@JsonProperty("attack") double attack,
			@JsonProperty("defenseSkill") double defenseSkill,
			@JsonProperty("shieldDefense") double shieldDefense) {
		this.armour = armour;
		this.morale = morale;
		this.speed = speed;
		this.attack = attack;
		this.defenseSkill = defenseSkill;
		this.shieldDefense = shieldDefense;
	}
	public double getArmour() {
		return armour*armourMult;
	}
	public double getMorale() {
		return morale*moraleMult;
	}
	public double getSpeed() {
		return speed*speedMult;
	}
	public double getAttack() {
		return attack*attackMult;
	}
	public double getDefenseSkill() {
		return defenseSkill;
	}
	public double getShieldDefense() {
		return shieldDefense;
	}
	
	// Should not be touched on original battle characteristic
	void addArmour(double armour) {
		this.armour += armour;
	}
	void addMorale(double morale) {
		this.morale += morale;
	}
	void addSpeed(double speed) {
		this.speed += speed;
	}
	void addAttack(double attack) {
		this.attack += attack;
	}
	void addDefenseSkill(double defenseskill) {
		this.defenseSkill += defenseskill;
	}
	void addShieldDefense(double shieldDefense) {
		this.shieldDefense += shieldDefense;
	}
	void addArmourmult(double armourmult) {
		this.armourMult += armourmult;
	}
	void addMoralemult(double moralemult) {
		this.moraleMult += moralemult;
	}
	void setSpeedmult(double speedmult) {
		this.speedMult += speedmult;
	}
	void setAttackmult(double attackmult) {
		this.attackMult += attackmult;
	}
}
