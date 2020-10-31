package util;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterable<T, R> implements Iterable<R>{
	private Function<? super T, ? extends  R> method;
	private Iterable<? extends T> subject;
	public MappingIterable(Iterable<? extends T> subject , Function <? super T,? extends R> method){
		this.method = method;
		this.subject = subject;
	}
	
	@Override
	public Iterator<R> iterator() {
		return new Iterator<R>(){
			Iterator<? extends T> subjectIter = subject.iterator();
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
