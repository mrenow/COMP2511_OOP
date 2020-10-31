package util;

import java.util.Map;
import java.util.Map.Entry;

public class DeserializableEntry<K,V> {
	private K key;
	private V value;
	public DeserializableEntry(Entry<K, V> entry){
		this.key = entry.getKey();
		this.value = entry.getValue();
	}
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
}