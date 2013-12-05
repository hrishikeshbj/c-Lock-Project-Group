package cLockServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The LockWalker is run periodically/ once every minute to update the timers on
 * the locks. If the timer on a lock is greater then threshold time, the lock is
 * revoked. Then the LockWalker traverses the locks and failed requests to find
 * deadlocks. At the end it cleans up the failed requests and quorum counters.
 * 
 * @author Nachiket
 * 
 */
public class cLockWalker implements Runnable {

	// Copy of ServerManager's DLM
	DLockManager dlm;

	// Copy of locks in ServerManager's DLM.
	Map<String, DLock> lockList;

	public cLockWalker() {
		this.dlm = cLockServerManager.dlm;
	}

	@Override
	public void run() {

		// System.out.println("LockWalker run");
		this.dlm = cLockServerManager.dlm;

		lockList = dlm.getLocks();
		if (lockList.size() > 0) {

			// list of empty dLocks that need to be cleared after run of the
			// lockWalker.
			List<String> emptyDLocks = new ArrayList<String>();

			// walk each dLock.
			for (Map.Entry<String, DLock> entry : lockList.entrySet()) {

				DLock dLock = entry.getValue();

				// increase the value of time for each simple lock of the dLock.
				dLock.updateLock();

				// List of stagnant simple Locks.
				List<String> stagnantLocks = new ArrayList<String>();

				for (SimpleLock sl : dLock.rLocks) {
					if (sl.lockTime >= 3) {
						stagnantLocks.add(sl.user);
					}
				}

				// clear the stagnant locks.
				for (String str : stagnantLocks) {
					dLock.releaseLock("sample", str, LockType.NL);
				}

				// if dLock is empty, add it to empty lockList for removal.
				if (dLock.rLocks.size() == 0) {
					emptyDLocks.add(dLock.rId);
				}
			}

			// remove the emptied dLocks
			for (String rId : emptyDLocks) {
				dlm.deleteLock(rId);
			}

			// deadlock Detection:
			for (Map.Entry<String, DLock> entry : lockList.entrySet()) {

				DLock dLock = entry.getValue();

				for (SimpleLock sl : dLock.rLocks) {

					boolean bl = findDeadLock(dLock.rId, sl.user, dLock.rId);
					if (bl) {
						sl.deadLock = true;
					}
				}
			}

			// for (Map.Entry<String, DLock> entry : lockList.entrySet()) {
			// DLock dLock = entry.getValue();
			// System.out.println(dLock.rId + " | " + dLock.state);
			// for (SimpleLock sl : dLock.rLocks) {
			// System.out.println("\t" + sl.user + " | "
			// + sl.lt.toString() + " | " + sl.lockTime + " | "
			// + sl.deadLock);
			// }
			// }
		}

		// cleanup.
		cLockServerManager.requests.clear();
		cLockServerManager.qc.clear();
	}

	private boolean findDeadLock(String rId, String user,
			String originalResource) {

		// Get the failed requests by the user.
		LockRequest lr = cLockServerManager.requests.get(user);

		if (lr != null) {

			// for each Resource the the user tried to lock,
			// if the resource is same as the recursion starting resource, there
			// is deadlock
			// else continue the recursion.
			for (String resource : lr.rList) {

				// DeadLock found.
				if (resource.equals(originalResource)) {
					return true;
				} else {
					
					// For all locks on the resource
					DLock dl = dlm.getLock(resource);
					if (dl != null) {

						// For each simple Lock in the DLock. 
						for (SimpleLock sl : dl.rLocks) {
							
							// Continue the recursion.
							if (findDeadLock(resource, sl.user,
									originalResource)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

}
