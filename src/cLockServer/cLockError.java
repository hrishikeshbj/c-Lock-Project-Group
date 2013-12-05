package cLockServer;

/**
 * Enumeration class to specify an error. These errors are passed as parameters
 * to operation performed and to used if severe.
 * 
 * @author Nachiket
 * 
 */
public enum cLockError {
	NE, // No Error.
	UE, // Unknown Error.
	IR, // Invalid Resource Id.
	IS, // Invalid State.
	LR, // Lock revoked or lock not present.
	DL; // Deadlock present.
}
