package util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * Why doesnt this exist in standard lib >:(
 *
 * Side note: This and the mapping iterator uses Iterator, Strategy and Composite patterns at the same time.
 * I swear I did this out of necessity (or more like convenience)
 * 
 * Oh, Im basically reimplementing the streams libraray with iterables
 * okay then.
 * @author ezra
 *
 * @param <T>
 */
public class Concatenator<T> implements Iterable<T>{	
	protected final Iterable<? extends Iterable<? extends T>> iterables;
	@SafeVarargs
	public Concatenator(T ... items) {
		iterables = List.of(List.of(items));
	}
	public Concatenator(Iterable<? extends T> i1, Iterable<? extends T> i2) {
		this.iterables = List.of(i1, i2);
	}
	public Concatenator(Iterable<? extends T> i1, Iterable<? extends T> i2, Iterable<? extends T> i3) {
		this.iterables = List.of(i1, i2, i3);
	}
	public Concatenator(Iterable<? extends T> i1, Iterable<? extends T> i2, Iterable<? extends T> i3, Iterable<? extends T> i4) {
		this.iterables = List.of(i1, i2, i3, i4);
	}
	public Concatenator(Iterable<? extends T> i1, Iterable<? extends T> i2, Iterable<? extends T> i3, Iterable<? extends T> i4, Iterable<? extends T> i5) {
		this.iterables = List.of(i1, i2, i3, i4, i5);
	}
	
	@SafeVarargs
	public Concatenator(Iterable<? extends T> ... iterables) {
		this.iterables = List.of(iterables);
	}
	
	public Concatenator(Iterable<? extends Iterable<? extends T>> iterables) {
		this.iterables = iterables;
	}
	
	public final Concatenator<T> and(Iterable<? extends Iterable<? extends T>> iterables) {
		return new Concatenator<T>(this, new Concatenator<T>(iterables)); 
	}
	
	@SafeVarargs
	public final Concatenator<T> and(Iterable<? extends T> ... iterables) {
		return new Concatenator<T>(this, new Concatenator<T>(iterables)); 
	}
	@SafeVarargs
	public final Concatenator<T> and(T ... items){
		return new Concatenator<T>(this, new Concatenator<T>(items));
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Iterator<? extends Iterable<? extends T>> parentIterator = iterables.iterator();
			// Set to empty iterator to handle init.
			Iterator<? extends T> childIterator = Collections.emptyIterator();
			// Assumes that hasNext will always be called before next.
			@Override
			public boolean hasNext() {
				while(!childIterator.hasNext()) {
					if(!parentIterator.hasNext()) {
						return false;
					}
					childIterator = parentIterator.next().iterator();
				}
				return true;
			}
			@Override
			public T next() {
				return childIterator.next();
			}
		};
	}
	public static void main(String[] args) {
		List<String> a = List.of("1", "2", "3");
		List<String> b = List.of("1", "2", "adsf");
		List<String> c = List.of("1", "dd", "3");
		List<String> d = List.of("1", "asdf", "3");
		List<String> e = List.of("32", "2", "yee", "haw");
		
		for (String s : new Concatenator<>(a,b,c).and(new Concatenator<>(d), new Concatenator<>(e))) {
			System.out.println(s);
		}
		for (Integer num: new Concatenator<>(1).and(2,3,4).and(5,6).and(7).and(8).and(9,10)) {
			System.out.println(num);
		}		
	}
}
