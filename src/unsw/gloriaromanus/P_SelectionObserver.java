package unsw.gloriaromanus;

import unsw.engine.Province;

public class P_SelectionObserver implements SelectionObserver {
    
    private Province p;
    private P_SelectionSubject selectionGrabber;
    private String p_name;
    private static int pID_Tracker = 0;
    private int p_ID;

    public P_SelectionObserver(P_SelectionSubject selectionGrabber) {
        this.selectionGrabber = selectionGrabber;
        this.p_ID = ++pID_Tracker;
        selectionGrabber.register(this);
    }

    @Override
    public void update(String p_name) {
        this.p_name = p_name;
    }
}
