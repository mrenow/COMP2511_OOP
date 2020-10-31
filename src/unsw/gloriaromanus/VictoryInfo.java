package unsw.gloriaromanus;

import java.util.List;

import unsw.gloriaromanus.VicCondition.*;

public class VictoryInfo {
    //winCondition;
    Double conquest;
    Double treasury;
    Double infrastructure;
    Double wealth;


    VicComponent vicConditions;

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
