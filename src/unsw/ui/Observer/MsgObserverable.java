package unsw.ui.Observer;

import java.util.ArrayList;
import java.util.List;

public class MsgObserverable implements Subject{
    private List<MsgObserver> observers = new ArrayList<>();
 
    @Override
    public void attach(MsgObserver o) {
        observers.add(o);
    }
 
    @Override
    public void detach(MsgObserver o) {
        observers.remove(o);
    }
 
    @Override
    public void notifyUpdate(Message m) {
        for(MsgObserver o: observers) {
            o.update(m);
        }
    }
}
