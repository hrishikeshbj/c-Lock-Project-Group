package cLockClient;

public enum LockType {
	NL, 		// Null lock 
	CR,			// Concurrent Read Lock 
	CW,			// Concurrent Write Lock 
	PR,			// Protected Read Lock 
	PW,			// Protected Write Lock 
	XL; 		// Exclusive Lock
	
}
