package unsw.gloriaromanus;

import java.util.Map;
import java.util.Map.Entry;

class DeserializableEntry<K,V> {
	private K key;
	private V value;
	DeserializableEntry(Entry<K, V> entry){
		this.key = entry.getKey();
		this.value = entry.getValue();
	}
	K getKey() {
		return key;
	}
	V getValue() {
		return value;
	}
}