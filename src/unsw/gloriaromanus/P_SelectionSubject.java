package unsw.gloriaromanus;

public interface P_SelectionSubject {
    
    public void register(P_SelectionObserver o);
    public void unregister(P_SelectionObserver o);
    public void notifyObserver();

}
