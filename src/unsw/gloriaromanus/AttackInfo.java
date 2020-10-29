package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.List;

/**
 * Information struct containing all the results of an attack
 * 
 */


public class AttackInfo {
    List<String> messages = new ArrayList<>();
    String result;
    public List<String> attackMsgs(){
        return this.messages;
    }
    
    public void addMsg(String msg){
        this.messages.add(msg);
    }

    public void setResult(String result){
        this.result = result;
    }
}
