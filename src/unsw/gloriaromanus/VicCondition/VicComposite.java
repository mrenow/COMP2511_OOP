package unsw.gloriaromanus.VicCondition;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class VicComposite implements VicComponent{
    private VictoryCondition goal=null;
    List<VicComponent> subgoals=new ArrayList<>();

    @JsonCreator
    public VicComposite(){}
    public VicComposite(VictoryCondition goal) {
        this.goal = goal;
    }
    public void addSubVic(VicComponent vic){
        this.subgoals.add(vic);
    }

    @Override
    public VictoryCondition getGoal(){
        return goal;
    }
//    public boolean checkvictory() {
//    	// check that conditions are satisfied here by checking victory on children
//    	// logic depends on AND or OR
//    	for (goal : subgoals) {
//  
//    		
//    	}
//    }
    @Override
    public boolean isLogic(){
        return true;
    }

    public List<VicComponent> getSubgoals(){
        return subgoals;
    }
}
