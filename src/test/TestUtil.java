package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Stream;

public class TestUtil {
	/**
	 * Asserts that the list <code>actualAttributes</code> is the same as the objects
	 * list when <code>attributeGetter</code> is applied to each object. Cares about list
	 * elements only. Cares about order.
	 * 
	 * Example:
	 * 
	 * <code>
	 * assertListAttributeEquals(List of names, List of Units, Unit::getName)
	 * </code>
	 * 
	 * @param actualAttributes
	 * @param objects
	 * @param attributeGetter
	 */
	public static <T,R> void assertListAttributeEquals(
			List<R> actualAttributes,
			List<T> objects,
			Function<? super T, ? extends R> attributeGetter){
		assertIterableEquals(
				actualAttributes
					.stream()
					.collect(Collectors.toList()),
				objects
					.stream()
					.map(attributeGetter)
					.collect(Collectors.toList())
					);
	}
	/**
	 * Same as assertListAttributeEquals, but order does not matter.
	 * @param actual
	 * @param objects
	 * @param attributeGetter
	 */
	public static <T,R> void assertCollectionAttributeEquals(
			Collection<R> actual,
			Collection<T> objects,
			Function<? super T, ? extends R> attributeGetter) {
		
		assertIterableEquals(
				actual
					.stream()
					.sorted()
					.collect(Collectors.toList()),
				objects
					.stream()
					.map(attributeGetter)
					.sorted()
					.collect(Collectors.toList())
					);
	}
	/**
	 * Asserts that two collections have the same elements. Does not care about order.
	 * @param actual
	 * @param objects
	 */
	public static void assertCollectionEquals(
			Collection<?> actual,
			Collection<?> objects) {
		assertIterableEquals(
				actual
					.stream()
					.sorted()
					.collect(Collectors.toList()),
				objects
					.stream()
					.sorted()
					.collect(Collectors.toList()));	
	}
	
	public static void main(String[] args) {
		HashSet<String> set1 = new HashSet<String>();
		set1.add("b");
		set1.add("a");
		set1.add("c");
		List<String> list1 = new ArrayList<String>();
		list1.add("a");
		list1.add("b");
		list1.add("c");
		
		// All these tests Succeed
		assertCollectionEquals(List.of("a", "c", "b"), set1);
		assertCollectionEquals(List.of("b", "a", "c"), list1);
		
		List<SampleObject> list2 = new LinkedList<SampleObject>();
		list2.add(new SampleObject(1, 3, "c"));
		list2.add(new SampleObject(6, 2, "b"));
		list2.add(new SampleObject(1, 36, "a"));
		// :: gets the instance method from SampleObject. Method is applied to each element of list2.
		assertCollectionAttributeEquals(List.of("a","b","c"), list2, SampleObject::getAttr3);
		assertCollectionAttributeEquals(List.of(1,1,6), list2, SampleObject::getAttr1);
		// order now matters
		assertListAttributeEquals(List.of(3,2,36), list2, SampleObject::getAttr2);
		
		
	}
}
