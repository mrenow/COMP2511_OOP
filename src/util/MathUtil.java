package util;

public class MathUtil {
	
	public static int min(int a, int b) {
		return a < b ? a : b; 	
	}
	
	public static double min(double a, double b) {
		return a < b ? a : b; 	
	}

	public static int max(int a, int b) {
		return a > b ? a : b; 	
	}
	
	public static double max(double a, double b) {
		return a > b ? a : b; 	
	}
		
	public static int constrain(int a, int min, int max) {
		return max(min(a, max),min);
	}
	
	public static double constrain(double a, double min, double max) {
		return max(min(a, max),min);
	}
	
	public static Integer min(Iterable<Integer> collection) {
		int runs = 0;
		Integer min = Integer.MAX_VALUE;
		for(Integer num : collection) {
			runs++;
			if(num < min) {
				min = num;
			}
			if(runs > 1000000) {
				System.err.print("Passed an infnite iterator?");
				for (StackTraceElement e :Thread.currentThread().getStackTrace()){
					System.err.println(e);
				}
				System.exit(1);
			}
		}
		return min;
		
	}

}
