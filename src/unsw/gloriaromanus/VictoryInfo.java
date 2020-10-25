package unsw.gloriaromanus;

public class VictoryInfo {
    //winCondition;
    // TODO choose: using percentage or outof 100 as count for victory
    float conquest;
    float treasury;
    float infrastructure;
    float wealth;

    public VictoryInfo(){}

    public boolean Victories(){return false;}
    
    public float getConquestGoal(){return this.conquest;}
    
    public float getTreasuryGoal(){return this.treasury;}
    
    public float getInfrastructureGoal(){return this.infrastructure;}

    public float getWealthGoal(){return this.wealth;}
    
}
