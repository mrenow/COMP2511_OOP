package unsw.ui.Observer;

public interface Subject {
    public void attach(MsgObserver o);
    public void detach(MsgObserver o);
    public void notifyUpdate(Message m);
}
