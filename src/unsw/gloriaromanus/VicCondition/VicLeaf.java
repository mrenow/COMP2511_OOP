package unsw.gloriaromanus.VicCondition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class VicLeaf implements VicComponent{
    private String goal="UNSETED";

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
