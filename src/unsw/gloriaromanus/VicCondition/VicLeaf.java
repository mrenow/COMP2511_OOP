package unsw.gloriaromanus.VicCondition;

public class VicLeaf implements VicComponent{
    private String goal;

    public VicLeaf(String goal) {
        this.goal = goal;
    }
    @Override
    public String getGoal(){
        return goal;
    }
    @Override
    public boolean isLogic(){
        return false;
    }
}
