package clockserver;

import static org.junit.Assert.*;
//
import org.junit.Test;

public class TestDLock {

	@Test
	public void test() {
		DLock dLock = new DLock("rId", "State1");
		boolean bl = dLock.createLock("State1", "user1", LockType.NL);
		if(bl == false) { fail(); }
		bl = dLock.createLock("State1", "user2", LockType.CR);
		if(bl == false) { fail(); }
		bl = dLock.createLock("State1", "user3", LockType.CW);
		if(bl == false) { fail(); }
		bl = dLock.createLock("State1", "user4", LockType.CW);
		if(bl == false) { fail(); }

		System.out.println(dLock.rId + " | " + dLock.state);
		for(SimpleLock sl : dLock.rLocks) {
			System.out.println("\t" + sl.user + " | " + sl.lt.toString() + " | " + sl.lockTime);
		}

		dLock.updateLock();
		dLock.updateLock();
		System.out.println(dLock.rId + " | " + dLock.state);
		for(SimpleLock sl : dLock.rLocks) {
			System.out.println("\t" + sl.user + " | " + sl.lt.toString() + " | " + sl.lockTime);
		}
		
		dLock.resetLock("State1", "user2", LockType.CR);
		dLock.updateLock();
		System.out.println(dLock.rId + " | " + dLock.state);
		for(SimpleLock sl : dLock.rLocks) {
			System.out.println("\t" + sl.user + " | " + sl.lt.toString() + " | " + sl.lockTime);
		}
	}

}
