package unsw.gloriaromanus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property="@id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.ANY)
public class BattleCharacteristic {
	
	double armour = 0;
	double morale = 0;
	double speed = 0;
	double attack = 0;
	double defenseSkill = 0;
	double shieldDefense = 0;
	
	double armourmult = 1;
	double moralemult = 1;
	double speedmult = 1;
	double attackmult = 1;  
	@JsonCreator
	private BattleCharacteristic() {}
	public BattleCharacteristic(double armour, double morale, double speed, double attack, double defenseSkill,
			double shieldDefense) {
		super();
		this.armour = armour;
		this.morale = morale;
		this.speed = speed;
		this.attack = attack;
		this.defenseSkill = defenseSkill;
		this.shieldDefense = shieldDefense;
	}

	public double getArmour() {
		return armour;
	}
	public double getMorale() {
		return morale;
	}
	public double getSpeed() {
		return speed;
	}
	public double getAttack() {
		return attack;
	}
	public double getDefenseSkill() {
		return defenseSkill;
	}
	public double getShieldDefense() {
		return shieldDefense;
	}
	
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
		this.armourmult += armourmult;
	}
	void addMoralemult(double moralemult) {
		this.moralemult += moralemult;
	}
	void setSpeedmult(double speedmult) {
		this.speedmult += speedmult;
	}
	void setAttackmult(double attackmult) {
		this.attackmult += attackmult;
	}
}
