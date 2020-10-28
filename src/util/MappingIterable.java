package util;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterable<T, R> implements Iterable<R>{
	private Function<T, R> method;
	private Iterable<T> subject;
	public MappingIterable(Iterable<T> subject , Function <T,R> method){
		this.method = method;
		this.subject = subject;
	}
	
	@Override
	public Iterator<R> iterator() {
		return new Iterator<R>(){
			Iterator<T> subjectIter = subject.iterator();
			@Override
			public boolean hasNext() {
				return subjectIter.hasNext();
			}
			@Override
			public R next() {
				return method.apply(subjectIter.next());
			}
		};
	}
}
