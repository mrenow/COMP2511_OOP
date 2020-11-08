package unsw.engine;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import unsw.engine.VicCondition.*;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class VictoryInfo {
    @JsonIdentityReference(alwaysAsId = true)
    VicComponent vic;

    @JsonCreator
    public VictoryInfo(){}

    public VictoryInfo(VicComponent vic){this.vic=vic;}

    public Boolean isVictory(){
        return vic.checkVic();
    }
    public Double getProgress(VictoryCondition vCondition){
        return vic.getProgress(vCondition);
    }
    public void update(VictoryCondition vCondition){
    }
}
