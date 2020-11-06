package unsw.engine.VicCondition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class VicLeaf implements VicComponent{
    private VictoryCondition goal=null;
    private Double progress = 0.0;

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
    public boolean checkVic(){
        return (progress>=1.0)?(true):(false);
    }
    void update(Double progress){
        this.progress = progress;
    }

    @Override
    public double getProgress(VictoryCondition vCondition) {
        if (vCondition==goal) {
            return progress;
        }
        return -1;
    }
}
