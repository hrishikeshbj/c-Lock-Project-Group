package clockserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class cLockWalker implements Runnable {

	DLockManager dlm;
	Map<String, DLock> lockList;

	public cLockWalker() {
		this.dlm = ServerManager.dlm;
	}

	@Override
	public void run() {

		System.out.println("LockWalker run");
		this.dlm = ServerManager.dlm;

		lockList = dlm.getLocks();
		if (lockList.size() > 0) {
			
			// list of empty dLocks that need to be cleared after run of the lockWalker.
			List<String> emptyDLocks = new ArrayList<String>();
			
			// walk each dLock.
			for (Map.Entry<String, DLock> entry : lockList.entrySet()) {
				
				DLock dLock = entry.getValue();
				
				// increase the value of time for each simple lock of the dLock.
				dLock.updateLock();
				
				// List of stagnant simple Locks.
				List<String> stagnantLocks = new ArrayList<String>();
				
				for (SimpleLock sl : dLock.rLocks) {
					if (sl.lockTime >= 5) {
						stagnantLocks.add(sl.user);
					}
				}
				
				// clear the stagnant locks.
				for (String str : stagnantLocks) {
					dLock.releaseLock("sample", str, LockType.NL);
				}
				
				// if dLock is empty, add it to empty lockList for removal.
				if(dLock.rLocks.size() == 0) {
					emptyDLocks.add(dLock.rId);
				}
			}
			
			// remove the emptied dLocks
			for (String rId: emptyDLocks) {
				dlm.deleteLock(rId);
			}

//			for (Map.Entry<String, DLock> entry : lockList.entrySet()) {
//				DLock dLock = entry.getValue();
//				System.out.println(dLock.resource + " | " + dLock.state);
//				for (SimpleLock sl : dLock.rLocks) {
//					System.out.println("\t" + sl.user + " | "
//							+ sl.lt.toString() + " | " + sl.lockTime + " | "
//							+ sl.deadLock);
//				}
//			}

			// deadlock Detection:
			for (Map.Entry<String, DLock> entry : lockList.entrySet()) {
				DLock dLock = entry.getValue();
				for (SimpleLock sl : dLock.rLocks) {
					boolean bl = findDeadLock(dLock.rId, sl.user,
							dLock.rId);
					if (bl) {
						sl.deadLock = true;
					}
				}
			}

			for (Map.Entry<String, DLock> entry : lockList.entrySet()) {
				DLock dLock = entry.getValue();
				System.out.println(dLock.rId + " | " + dLock.state);
				for (SimpleLock sl : dLock.rLocks) {
					System.out.println("\t" + sl.user + " | "
							+ sl.lt.toString() + " | " + sl.lockTime + " | "
							+ sl.deadLock);
				}
			}
		}
	}

	private boolean findDeadLock(String rId, String user,
			String originalResource) {
		LockRequest lr = ServerManager.requests.get(user);
		if (lr != null) {
			for (String resource : lr.rList) {
				if (resource.equals(originalResource)) {
					return true;
				} else {
					DLock dl = dlm.getLock(resource);
					if (dl != null) {
						for (SimpleLock sl : dl.rLocks) {
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
