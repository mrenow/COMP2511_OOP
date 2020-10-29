package util;

import java.util.ArrayList;
import java.util.List;
/**
 * Basic Observer framework
 * 
 * @author ezra
 *
 * @param <T>
 */
public abstract class ChangeNotifier<T>{
	private List<ChangeListener<T>> listeners = new ArrayList<ChangeListener<T>>();

	public void addListener(ChangeListener<T> l) {
		listeners.add(l);
	}
	public boolean removeListener(ChangeListener<T> l) {
		return listeners.remove(l);
	}
	
	public void notifyListeners(T state) {
		listeners.forEach((listener)->listener.update(state));
	}

}
