package unsw.engine.VicCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Override
    public boolean checkVic() {
        if (goal == null) {
            System.out.println("no goal move on");
            return false;
        }
        switch (goal) {
            case AND:
                for (VicComponent vicComponent : subgoals) {
                    if (!vicComponent.checkVic()) {
                        return false;
                    }
                }
                return true;
            case OR:
                for (VicComponent vicComponent : subgoals) {
                    if (vicComponent.checkVic()) {
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    public List<VicComponent> getSubgoals(){
        return subgoals;
    }

    public void update(VictoryCondition vCondition, Double progress){
        for (VicComponent vicComponent : subgoals) {
            vicComponent.update(vCondition, progress);
        }
    }

    @Override
    public double getProgress(VictoryCondition vCondition) {
        double out;
    	for (VicComponent vicComponent : subgoals) {
            
            switch (vicComponent.getGoal()) {
                case OR:
                case AND:
                    out = vicComponent.getProgress(vCondition);
                    if(out >= 0) {
                    	// found relevant condition
                    	return out;
                    }else {
                    	continue;
                    }
                default:
                    if (vicComponent.getGoal().equals(vCondition)) {
                        return vicComponent.getProgress(vCondition);
                    }
                    continue;
            }
            
        }
        return -1;
    }
    @Override
    public double getMainProgress(){
        Double prog = 0.0;
        for (VicComponent vicComponent : subgoals) {
	        switch (goal) {
	            case AND:
	                prog += vicComponent.getMainProgress()/2;
	                continue;
	            case OR:
	                prog = Double.max(prog, vicComponent.getMainProgress());
	                continue;
	            default:
	                return -1; // should not run!!
	        }
        }
        return prog;
    }
	@Override
	public String toString() {
		return "(" + String.join(" " + goal.toString() + " ", subgoals.stream().map(Object::toString).collect(Collectors.toList())) + ")";
	}
}
