package unsw.gloriaromanus;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import unsw.gloriaromanus.VicCondition.*;

public class VictoryInfo {
    //winCondition;
    Double conquest=0.0;
    Double treasury=0.0;
    Double infrastructure=0.0;
    Double wealth=0.0;


    VicComponent vicConditions;

    @JsonCreator
    public VictoryInfo(){}

    public VictoryInfo(VicComponent vic){this.vicConditions=vic;}

    public Boolean isVictory(){
        if (checkVic(this.vicConditions)) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkVic(VicComponent vic){
        List<VicComponent> subgoals;
        VicComposite vicCom;
        if (vic==null) {
            System.out.println("must have preset victory condition");
            return true;
        }
        switch (vic.getGoal()) {
            case "AND":
                vicCom = (VicComposite)vic;
                subgoals = vicCom.getSubgoals();
                for (VicComponent vicComponent : subgoals) {
                    if (!checkVic(vicComponent)) {
                        return false;
                    }
                }
                return true;
            case "OR":
                vicCom = (VicComposite)vic;
                subgoals = vicCom.getSubgoals();
                for (VicComponent vicComponent : subgoals) {
                    if (checkVic(vicComponent)) {
                        return true;
                    }
                }
                return false;
            case "CONQUEST":
                if (conquest>=1.0) {
                    return true;
                } else {
                    return false;
                }
            case "TREASURY":
                if (treasury>=1.0) {
                    return true;
                } else {
                    return false;
                }
            case "WEALTH":
                if (wealth>=1.0) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    public Double getConquest() {
        return conquest;
    }

    public void setConquest(Double conquest) {
        this.conquest = conquest;
    }

    public Double getTreasury() {
        return treasury;
    }

    public void setTreasury(Double treasury) {
        this.treasury = treasury;
    }

    public Double getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(Double infrastructure) {
        this.infrastructure = infrastructure;
    }

    public Double getWealth() {
        return wealth;
    }

    public void setWealth(Double wealth) {
        this.wealth = wealth;
    }
}
