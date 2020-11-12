package unsw.ui.Observer;

public interface Subject <T>{
    public void attach(Observer <T> o);
    public void detach(Observer <T> o);
    public void notifyUpdate(T m);
}
