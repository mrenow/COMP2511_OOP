package util;

public class MathUtil {
	
	public static int min(int a, int b) {
		return a < b ? a : b; 	
	}
	
	public static double min(double a, double b) {
		return a < b ? a : b; 	
	}
	
	public static <T extends Comparable<T>> T min(T a, T b){
		return a.compareTo(b) < 0 ? a : b;
	} 

	public static int max(int a, int b) {
		return a > b ? a : b; 	
	}
	
	public static double max(double a, double b) {
		return a > b ? a : b; 	
	}
	
	public static <T extends Comparable<T>> T max(T a, T b){
		return a.compareTo(b) > 0 ? a : b;
	} 
	
	public static int constrain(int a, int min, int max) {
		return max(min(a, max),min);
	}
	
	public static double constrain(double a, double min, double max) {
		return max(min(a, max),min);
	}
	
	public static <T extends Comparable<T>> T constrain(T a, T min, T max) {
		return max(min(a, max),min);
	}

}
