package unsw.gloriaromanus;

import java.util.Random;

public class GlobalRandom {
	public static Random generator;
	public static void init(long seed) {
		generator = new Random(seed);
	}
	public static void init() {
		generator = new Random();
	}

	public static int nextInt() {
		return generator.nextInt();
	}
	public static int nextInt(int a) {
		return generator.nextInt(a);
	}
	
	public static int nextInt(int a, int b) {
		return a + generator.nextInt(b-a);
	}
	
	public static double nextUniform() {
		return generator.nextDouble();
	}
	public static double nextGaussian() {
		return generator.nextGaussian();
	}
}
