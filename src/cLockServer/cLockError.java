package clockserver;

public enum cLockError {
	NE, 		// No Error.
	UE,			// Unknown Error.
	IR,			// Invalid Resource Id.
	IS,			// Invalid State.
	LR;			// Lock revoked or lock not present.
	
}
