package unsw.gloriaromanus.VicCondition;

import java.util.ArrayList;
import java.util.List;

public class VicComposite implements VicComponent{
    private String goal;
    List<VicComponent> subgoals;

    public VicComposite(String goal) {
        this.goal = goal;
        this.subgoals = new ArrayList<>();
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
