package unsw.gloriaromanus.VicCondition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
    boolean isLogic();
}
