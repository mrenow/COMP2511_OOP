package unsw.gloriaromanus;

/**
 * Thrown when an unhandlable exception occurs while constructing a GameController
 */
public class DataInitializationException2 extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataInitializationException2(String message) {
		super(message);
	}
	public DataInitializationException2(String message, Exception e) {
		super(message, e);
	}

}
 