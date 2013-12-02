package clockserver;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DLockManager {
	private Map<String, DLock> lockList;
	private ResourceTableManager rtm;

	public DLockManager(ResourceTableManager rtm) {
		lockList = new HashMap<String, DLock>();
		this.rtm = rtm;
	}
	
	public Map<String, DLock> getLocks() {
		return lockList;
	}
	
	public DLock getLock(String rId) {
		return lockList.get(rId);
	}
	
	public boolean canCreate(String rId, String rState, String user, LockType lt) throws SQLException {

		DLock dLock = lockList.get(rId);
		if (dLock != null) {
			return dLock.canCreate(rState, user, lt);
		}
		return true;
	}

	public cLockError createLock(String rId, String state, String user,
			LockType lt) throws SQLException {

		// search the state in DB
		List<Resource> rs = rtm.getResource(rId);
		if (rs.size() <= 0) {
			return cLockError.IR;
		}
		boolean stateFound = false;
		for (Resource r : rs) {
			if (r.getrState().equals(state)) {
				stateFound = true;
			}
		}
		// if state is not found return false.
		if (!stateFound) {
			return cLockError.IS;
		}

		// search for lock in list of current locks.
		DLock dLock = lockList.get(rId);
		if (dLock == null) {

			// create the lock
			dLock = new DLock(rId, state);
			dLock.createLock(state, user, lt);
			lockList.put(rId, dLock);
			return cLockError.NE;
		} else {
			if (dLock.createLock(state, user, lt)) {
				lockList.put(rId, dLock);
				return cLockError.NE;
			}
		}
		return cLockError.UE;
	}

	public void releaseLock(String rId, String state, String user, LockType lt) {

		DLock dLock = lockList.get(rId);
		if (dLock != null) {
			dLock.releaseLock(state, user, lt);
		}
	}
	
	public void updateLock(String rId, String state, String user, LockType lt) {

		DLock dLock = lockList.get(rId);
		if (dLock != null) {
			dLock.updateLock();
		}
	}
	
	public cLockError resetLock(String rId, String state, String user, LockType lt) {

		DLock dLock = lockList.get(rId);
		if (dLock != null) {
			dLock.resetLock(state, user, lt);
		} else {
			return cLockError.LR;
		}
		return cLockError.NE;
	}
	
	public void deleteLock(String rId) {
		lockList.remove(rId);
	}
}
