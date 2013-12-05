package cLockClient;

/**
 * Enumeration of the lock errors. These lock errors are used to specify the
 * status of the lock.
 * 
 * 
 * 
 */
public enum LockError {

	LK, // Lock is maintained.
	LI, // Lock is invoked by server.
	DL; // possible deadlock.
}
