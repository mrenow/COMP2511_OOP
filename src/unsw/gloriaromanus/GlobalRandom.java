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
}
