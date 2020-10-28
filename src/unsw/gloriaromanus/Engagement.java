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

    
}
