package unsw.gloriaromanus.VicCondition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class VicLeaf implements VicComponent{
    private VictoryCondition goal=null;

    @JsonCreator
    public VicLeaf(){}
    public VicLeaf(VictoryCondition goal) {
        this.goal = goal;
    }
    @Override
    public VictoryCondition getGoal(){
        return goal;
    }
    @Override
    public boolean isLogic(){
        return false;
    }
}
