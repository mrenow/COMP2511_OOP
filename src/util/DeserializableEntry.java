package util;

import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, creatorVisibility = Visibility.ANY)
public class DeserializableEntry<K,V> {
	private K key;
	private V value;
	
	@JsonCreator
	private DeserializableEntry() {}
	
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