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
    @Override
    public void update(VictoryCondition vc, Double progress){
        if (vc.equals(this.goal)) {
            this.progress=progress;
        }
    }

    @Override
    public double getProgress(VictoryCondition vCondition) {
        // if (vCondition.equals(goal)) {
        //     return progress;
        // }
        // return -1;
        return this.progress;
    }
    @Override
    public String toString() {
    	return goal.toString();
    }
	@Override
	public double getMainProgress() {
		
		return progress;
	}
}
