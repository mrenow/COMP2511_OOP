package unsw.gloriaromanus;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

public class VictoryInfo {
    //winCondition;
    // TODO choose: using percentage or outof 100 as count for victory
    Double conquest;
    Double treasury;
    Double infrastructure;
    Double wealth;

    public VictoryInfo(){}

    public Boolean isVictory(){
        return false;
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
