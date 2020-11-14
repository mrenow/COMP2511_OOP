package unsw.engine.VicCondition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import util.ArrayUtil;

import static unsw.engine.VicCondition.VictoryCondition.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = VicComposite.class, name = "VicComposite"),
    @JsonSubTypes.Type(value = VicLeaf.class, name = "VicLeaf")
})
public interface VicComponent {
	VictoryCondition getGoal();
    boolean checkVic();
    double getMainProgress();
    double getProgress(VictoryCondition vCondition);
    void update(VictoryCondition vc,Double progress);
    
    public static VicComponent randVicComponent(List<VictoryCondition> values, Random r) {
    	if(values.size() < 4) {
    		return randVicLeaf(values, r);
    	}
    	VictoryCondition goal = ArrayUtil.selectRandom(values, r);
    	switch(goal) {
	    case AND:
	    case OR:
	    	VicComposite out = new VicComposite(goal);
	    	// force first component to be a leaf
	    	out.addSubVic(randVicLeaf(values, r));
	    	// second could be leaf or composite
	    	out.addSubVic(randVicComponent(values, r));
	    	return out;
	    case CONQUEST:
	    case TREASURY:
	    case WEALTH:
	    	values.remove(goal);
	    	return new VicLeaf(goal);
	    default:
	    	return null;
    	}
    }
    
    public static VicLeaf randVicLeaf(List<VictoryCondition> values, Random r) {
    	List<VictoryCondition> leafValues = new ArrayList<>(values);
    	leafValues.removeAll(List.of(AND,OR));
    	VictoryCondition goal = ArrayUtil.selectRandom(leafValues, r);
    	values.remove(goal);
    	return new VicLeaf(goal);
    }
    
}
