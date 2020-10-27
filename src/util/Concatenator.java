package util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * Why doesnt this exist in standard lib >:(
 * @author ezra
 *
 * @param <T>
 */
public class Concatenator<T> implements Iterable<T>{	
	List<List<T>> lists;

	@SafeVarargs
	public Concatenator(List<T> ... lists) {
		this.lists = List.of(lists);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new ConcatenateIterator();
	}
	
	public class ConcatenateIterator implements Iterator<T> {
		Iterator<List<T>> 	parentIterator = lists.iterator();
		// Set to empty iterator to handle init.
		Iterator<T> childIterator = Collections.emptyIterator();
		@Override
		public boolean hasNext() {
			return parentIterator.hasNext() || childIterator.hasNext();
		}
		@Override
		public T next() {
			while(!childIterator.hasNext()) {
				childIterator = parentIterator.next().iterator();
			}
			return childIterator.next();
		}
	}
	/*public static void main(String[] args) {
		List<String> a = List.of("1", "2", "3");
		List<String> b = List.of("1", "2", "adsf");
		List<String> c = List.of("1", "dd", "3");
		List<String> d = List.of("1", "asdf", "3");
		List<String> e = List.of("32", "2", "yee", "haw");
		
		for (String s : new Concatenator<>(a,b,c,d,e)) {
			System.out.println(s);
		}
		
	}*/
}
