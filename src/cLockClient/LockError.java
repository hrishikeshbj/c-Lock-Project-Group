package cLockClient;

public enum LockError {

	LK, 		// Lock is maintained.
	LI,			// Lock is invoked by server.
	DL;			// possible deadlock.
}
