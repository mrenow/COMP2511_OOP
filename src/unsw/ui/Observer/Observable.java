package unsw.ui.Observer;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> implements Subject<T>{
    private List<Observer<T>> observers = new ArrayList<>();
 
    @Override
    public void attach(Observer<T> o) {
        observers.add(o);
    }
 
    @Override
    public void detach(Observer<T> o) {
        observers.remove(o);
    }
 
    @Override
    /* Call this function to trigger an event.
     * It will then cause all the observers observing this object
     * to call their update functions
     */ 
    public void notifyUpdate(T m) {
        for(Observer<T> o: observers) {
            o.update(m);
        }
    }
}
