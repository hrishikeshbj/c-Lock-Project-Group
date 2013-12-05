package cLockClient;

/**
 * Exception to be thrown by the cLock client when an error occurs.
 * 
 * @author Nachiket
 * 
 */
public class LockException extends Exception {

	public LockException(String string) {
		super(string);
	}
}
