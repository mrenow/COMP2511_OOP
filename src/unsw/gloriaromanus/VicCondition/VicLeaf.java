package unsw.gloriaromanus.VicCondition;

import com.fasterxml.jackson.annotation.JsonCreator;

public class VicLeaf implements VicComponent{
    private String goal=null;

    @JsonCreator
    public VicLeaf(){}
    public VicLeaf(String goal) {
        this.goal = goal;
    }
    @Override
    public String getGoal(){
        return goal;
    }
    @Override
    public boolean isLogic(){
        return false;
    }
}
