package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import util.MathUtil;

public class GlobalRandom {
	private static long currentSeed = new Random().nextLong();
	public static Random generator = new Random(currentSeed);
	private static StringBuilder randomLog = new StringBuilder("Seed: " + currentSeed + "\n");
	public static void init(long seed) {
		currentSeed = seed;
		randomLog = new StringBuilder("Seed: " + seed + "\n");
		generator = new Random(seed);
	}

	public static void init() {
		currentSeed = new Random().nextLong();
		randomLog = new StringBuilder("Seed: " + currentSeed + "\n");
		generator = new Random(currentSeed);
	}
	
	public static long getSeed() {
		return currentSeed;
	}

	public static int nextInt() {
		return log(generator.nextInt(), "nextInt()");
	}

	public static int nextInt(int a) {
		return log(generator.nextInt(a), "nextInt(" + a + ")");
	}

	public static int nextInt(int a, int b) {
		return log(a + generator.nextInt(b - a), "nextInt(" + a + "," + b + ")");
	}

	public static double nextUniform() {
		return log(generator.nextDouble(), "nextUniform()");
	}

	public static double nextGaussian() {
		return log(generator.nextGaussian(), "nextGaussian()");
	}
	public static <T> T getRandom(List<T> list) {
		return list.get(nextInt(list.size()));		
	}
	public static <T> T removeRandom(List<T> list) {
		return list.remove(nextInt(list.size()));		
	}
	public static String getLog() {
		return randomLog.toString();
	}
	/*
	 * test area for getting seeds
	 * 
	 */
	public static void main(String [] args) {
		List<Condition<?>> randFuncs = new ArrayList<>();
		randFuncs.add(C(GlobalRandom::nextUniform, 0.05, 0.1));
		randFuncs.add(C(GlobalRandom::nextUniform, 0.1, 0.15));
		randFuncs.add(C(GlobalRandom::nextUniform, 0.15, 0.2));
		randFuncs.add(C(GlobalRandom::nextGaussian, 0.2, 0.25));
		randFuncs.add(C(GlobalRandom::nextUniform, 0.25, 0.3));
		randFuncs.add(C(GlobalRandom::nextUniform, 0.3, 0.35));
		init(getSeedSatisfying(randFuncs));
		// forces the random things to satisfy given constraints
		System.out.println(nextUniform());
		System.out.println(nextUniform());
		System.out.println(nextUniform());
		System.out.println(nextGaussian());
		System.out.println(nextUniform());
		System.out.println(nextUniform());
	}
	
	
	private static <T extends Comparable<T>> Condition<T> C(Supplier<T> randFun, T min, T max) {
		return new Condition<T>(randFun, min, max);
	}
	
	private static  long getSeedSatisfying(List<Condition<?>> conditions) {
		Random r = new Random();
		long seed; 
		while(!trySeed(conditions, seed = r.nextLong()));
		return seed;
	}
	private static boolean trySeed(List<Condition<?>> conditions, long seed) {
		init(seed);
		for (Condition<?> c : conditions) {
			if(!c.verify()) {
				return false;		
			}
		}
		return true;
	}
	private static int log(int val, String name) {
		randomLog.append(name + "\t: " + val + "\n");
		return val;
	}
	private static double log(double val, String name) {
		randomLog.append(name + "\t: " + val + "\n");
		return val;
	}
}

class Condition<T extends Comparable<T>> {
	private Supplier<T> randFun;
	private T max;
	private T min;
	
	public Condition(Supplier<T> randFun, T min, T max) {
		super();
		this.randFun = randFun;
		this.max = max;
		this.min = min;
	}
	
	public boolean verify() {
		T result = randFun.get();
		return min.compareTo(result) <= 0 &&  result.compareTo(max) <= 0;
	}
	
}
