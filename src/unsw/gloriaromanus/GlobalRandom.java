package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

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
		return a + generator.nextInt(b - a);
	}

	public static double nextUniform() {
		return generator.nextDouble();
	}

	public static double nextGaussian() {
		return generator.nextGaussian();
	}
	
	
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
}

class Condition<T extends Comparable<T>> {
	Supplier<T> randFun;
	T max;
	T min;
	
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
