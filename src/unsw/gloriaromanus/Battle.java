package unsw.gloriaromanus;

import java.util.List;
import java.util.Random;

public class Battle {
    //basic info of a battle
    List<Unit> attackers;
    List<Unit> defenders;
    int numEngage = 0;

    //data for setup a engagement
    Unit attacker;
    Unit defender;
    EngagementData aData;
    EngagementData dData;
    //current engagement
    Engagement engagement;
    public Battle() {
    }

    public Battle(List<Unit> attackers, List<Unit> defenders) {
        this.attackers = attackers;
        this.defenders = defenders;
	}

	public boolean getResult(){

        while (!isBattleEnd()) {
            //setupengagement data for unit
            setUp();
            //create engagement
            Engagement engagement = new Engagement(aData, dData);
            //checkresult and do other stuff
            engagement.result();
            //flee route breaking unitdead stuff...
            this.numEngage++;
        }
        // default: attacker never wins
        return false;
    }

    private void setUp(){
        //read in buffs
        //TODO
        //random choose two units
        this.attacker = pickUnit(this.attackers);
        this.defender = pickUnit(this.defenders);
        this.aData = new EngagementData();
        this.dData = new EngagementData();
        //setupdata

        //do some change to data
    }

    /**
     * pick a unit from Unit lists
     * @param units
     * @return unit choosed from list
     */
    private Unit pickUnit(List<Unit> units){
        Random random = new Random();
        //uniformly randomed
        int index = random.nextInt(units.size());
        return units.get(index);
    }

    /**
     * check if the end of battle condition reached
     * @return if the battle ended
     */
    private boolean isBattleEnd(){
        //TODO for now battle never start
        return true;
    }
}
