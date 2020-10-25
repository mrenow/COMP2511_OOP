package unsw.gloriaromanus;

/**
 * Thrown when an unhandlable exception occurs while constructing a GameController
 */
public class DataInitializationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataInitializationException(String message) {
		super(message);
	}
	public DataInitializationException(String message, Exception e) {
		super(message, e);
	}

}
 