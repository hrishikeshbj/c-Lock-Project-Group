package cLockServer;

import java.util.List;

/**
 * Container class for a Lock
 * @author Nachiket
 *
 */

public class DLock {
	
	String resource;
	String state;
	List<Lock> rLocks;
	public class Lock {
		String user;
		LockType lt;
		int locktime;		
	}
	
	public static enum LockType {
		NL, 		// Null lock 
		CR,			// Concurrent Read Lock 
		CW,			// Concurrent Write Lock 
		PR,			// Protected Read Lock 
		PW,			// Protected Write Lock 
		XL; 		// Exclusive Lock
		
	}
	
	public LockType getHighestLock() {
		Lock hLock = rLocks.get(0);
		for(Lock l : rLocks) {
			if(hLock.lt.compareTo(l.lt) < 0) {
				hLock = l;
			}
		}
		return hLock.lt;
	}
}
