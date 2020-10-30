package util;

import java.util.Iterator;

/**
 * Im going to make a library of these and use them in every java project
 * @author ezra
 *
 * @param <T>
 */
public class Repeat<T> implements Iterable<T>{
	private T element;
	private int times;
	
	public Repeat(T element, int times) {
		this.element = element;
		this.times = times;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int remaining = times;
			@Override
			public boolean hasNext() {
				return remaining > 0;
			}
			@Override
			public T next() {
				remaining --;
				return element;
			}
		};
	}

}
