package unsw.engine;

/**
 * Thrown when an unhandlable exception occurs while constructing a GameController
 */
public class DataInitializationError extends Error{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataInitializationError(String message) {
		super(message);
	}
	public DataInitializationError(String message, Exception e) {
		super(message, e);
	}
	
	public void printAndTerminate() {
		System.err.print(getMessage());
		printStackTrace();
		System.exit(1);
	}

}
 