package util;

public class MathUtil {
	
	public int min(int a, int b) {
		return a < b ? a : b; 	
	}
	
	public double min(double a, double b) {
		return a < b ? a : b; 	
	}
	
	public <T extends Comparable<T>> T min(T a, T b){
		return a.compareTo(b) < 0 ? a : b;
	} 

	public int max(int a, int b) {
		return a > b ? a : b; 	
	}
	
	public double max(double a, double b) {
		return a > b ? a : b; 	
	}
	
	public <T extends Comparable<T>> T max(T a, T b){
		return a.compareTo(b) > 0 ? a : b;
	} 
	
	public int constrain(int a, int max, int min) {
		return max(min(a, max),min);
	}
	
	public double constrain(double a, double max, double min) {
		return max(min(a, max),min);
	}
	
	public <T extends Comparable<T>> T constrain(T a, T max, T min) {
		return max(min(a, max),min);
	}

}
