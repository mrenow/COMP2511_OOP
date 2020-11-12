package unsw.engine.VicCondition;

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
            if (vicComponent.getGoal()==vCondition) {
                VicLeaf vl =(VicLeaf)vicComponent;
                vl.update(progress);
            }
        }
    }

    @Override
    public double getProgress(VictoryCondition vCondition) {
        for (VicComponent vicComponent : subgoals) {
            if (vicComponent.getGoal()==vCondition) {
                return vicComponent.getProgress(vCondition);
            }
        }
        return -1;
    }
    public Double getMainProgress(){
        Double prog = 0.0;
        VicComposite temp;
        Double max = 0.0;
        switch (goal) {
            case AND:
                for (VicComponent vicComponent : subgoals) {
                    switch (vicComponent.getGoal()) {
                        case AND:
                            temp = (VicComposite)vicComponent;
                            prog += temp.getMainProgress();
                            break;
                        case OR:
                            temp = (VicComposite)vicComponent;
                            max = Double.max(max, temp.getMainProgress());
                            break;
                        
                        default:
                            prog += 0.5*vicComponent.getProgress(vicComponent.getGoal());
                            break;
                    }
                }
                prog += max*0.5;
                break;
            case OR:
                for (VicComponent vicComponent : subgoals) {
                    switch (vicComponent.getGoal()) {
                        case AND:
                            temp = (VicComposite)vicComponent;
                            prog += temp.getMainProgress();
                            break;
                        case OR:
                            temp = (VicComposite)vicComponent;
                            max = Double.max(max, temp.getMainProgress());
                            break;
                        default:
                            vicComponent.getProgress(vicComponent.getGoal());
                            break;
                    }
                }
                prog += max*0.5;
                break;
            default:
                break;
        }
        return prog;
    }
}
