package cLockServer;

import java.util.HashMap;

public class LockWalker extends Thread{

	HashMap<String, DLock> dLocks;
	
	LockWalker(HashMap<String, DLock> dLocks) {
		this.dLocks = dLocks;	
	}
	
	public void run() {
		
	}
	
	public void incLockTime(DLock dLock) {
		
	}
	
	public void detectDeadlock(DLock dLock) {
		
	}
	
}
