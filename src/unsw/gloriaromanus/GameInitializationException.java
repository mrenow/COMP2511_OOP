package unsw.gloriaromanus;

/**
 * Thrown when an unhandlable exception occurs while constructing a GameController
 */
public class GameInitializationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GameInitializationException(String message) {
		super(message);
	}
	public GameInitializationException(String message, Exception e) {
		super(message, e);
	}

}
 