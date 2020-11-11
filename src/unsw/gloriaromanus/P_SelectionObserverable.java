package unsw.gloriaromanus;

import java.util.ArrayList;

import unsw.engine.Province;

public class P_SelectionObserverable implements P_SelectionSubject {

    public ArrayList<P_SelectionObserver> observer;
    public Province p;
    public String p_name;

    public void SelectionGrabber() {
        observer = new ArrayList<P_SelectionObserver>();
        p_name = p.getName();
    }

    @Override
    public void register(P_SelectionObserver newObserver) {
        observer.add(newObserver);
    }

    @Override
    public void unregister(P_SelectionObserver deleteObserver) {
        observer.remove(deleteObserver);
    }

    @Override
    public void notifyObserver() {
        for (P_SelectionObserver o : observer) {
            o.update(p_name);
        }
    }

    public void setPName(String p_name) {
        this.p_name = p_name;
        notifyObserver();
    }
    
}
