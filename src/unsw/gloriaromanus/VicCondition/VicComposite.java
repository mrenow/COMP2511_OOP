package unsw.gloriaromanus.VicCondition;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class VicComposite implements VicComponent{
    private String goal="UNSETED";
    List<VicComponent> subgoals=new ArrayList<>();

    @JsonCreator
    public VicComposite(){}
    public VicComposite(String goal) {
        this.goal = goal;
    }
    public void addSubVic(VicComponent vic){
        this.subgoals.add(vic);
    }

    @Override
    public String getGoal(){
        return goal;
    }
    @Override
    public boolean isLogic(){
        return true;
    }

    public List<VicComponent> getSubgoals(){
        return subgoals;
    }
}
