package cLockServer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In memory manager of list of DLocks maintained by the cLock Engine. THis
 * manager maps the calls from user to the low level calls of locks.
 * 
 * @author Nachiket
 * 
 */
public class DLockManager {

	/**
	 * Map of DLocks maintained by the server. The locks are mapped with
	 * Resource Id as key to an instance of DLock.
	 */
	private Map<String, DLock> lockList;

	/***
	 * Resource Table manager. This table manager is used to check the state of
	 * the request against the states registered with the server by the user.
	 */
	private ResourceTableManager rtm;

	/**
	 * Initialize the Lock Manager.
	 * 
	 * @param rtm
	 *            : Resource Table manager.
	 */
	public DLockManager(ResourceTableManager rtm) {

		lockList = new HashMap<String, DLock>();
		this.rtm = rtm;
	}

	/**
	 * Retrieve all the locks in memory. This call is made by LockWalker for
	 * deadlock detection.
	 * 
	 * @return : Returns the map of locks
	 */
	public Map<String, DLock> getLocks() {
		return lockList;
	}

	/**
	 * Search for a particular DLock with resource id and return it.
	 * 
	 * @param rId
	 *            : Unique resource id to search the DLock with.
	 * @return : the DLock if found or null if not found.
	 */
	public DLock getLock(String rId) {
		return lockList.get(rId);
	}

	/**
	 * Map the call to can create lock from Server Manager to particular DLock.
	 * 
	 * @param rId
	 *            : Unique Resource Id mentioned by user in the request
	 * @param rState
	 *            : State of the resource mentioned by user in the request to
	 *            lock the request with.
	 * @param user
	 *            : Unique user Id mentioned by user in the request
	 * @param lt
	 *            : Lock Type mentioned by user in the request to lock the
	 *            request with.
	 * @return : true if the lock can be created else false
	 * @throws SQLException
	 *             : If error occurred while searching the resource in database.
	 */
	public boolean canCreate(String rId, String rState, String user, LockType lt)
			throws SQLException {

		// Search the DLock in the DLock map.
		DLock dLock = lockList.get(rId);
		if (dLock != null) {

			// check whether the lock can be created in the DLock.
			return dLock.canCreate(rState, user, lt);
		}
		return true;
	}

	/**
	 * Create a lock for the user with given parameters on a resource mentioned
	 * in the request by the user. Map the call from Server Manager to DLock's
	 * create lock.
	 * 
	 * @param rId
	 *            : Unique Resource Id mentioned by user in the request
	 * @param rState
	 *            : State of the resource mentioned by user in the request to
	 *            lock the request with.
	 * @param user
	 *            : Unique user Id mentioned by user in the request
	 * @param lt
	 *            : Lock Type mentioned by user in the request to lock the
	 *            request with.
	 * @return : true if the lock is created else false
	 * @throws SQLException
	 *             : If error occurred while searching the resource in database.
	 */
	public cLockError createLock(String rId, String state, String user,
			LockType lt) throws SQLException {

		// search the state in DB
		List<Resource> rs = rtm.getResource(rId);

		// If resource is not registered, return invalid resource
		if (rs.size() <= 0) {
			return cLockError.IR;
		}

		// searc hthe state mentioned in request with the registered states.
		boolean stateFound = false;
		for (Resource r : rs) {
			if (r.getrState().equals(state)) {
				// State found in registered states.
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

			// create the Dlock
			dLock = new DLock(rId, state);

			// add the simple lock.
			dLock.createLock(state, user, lt);

			// add the Dlock to the serverManager's lockList.
			lockList.put(rId, dLock);
			return cLockError.NE;

		} else {

			// THe DLock is present, just add the simpleLock to this DLock.
			if (dLock.createLock(state, user, lt)) {

				// update the DLock in the serverManager's lockList.
				lockList.put(rId, dLock);
				return cLockError.NE;
			}
		}
		// Something is wrong!! Don't know what.
		// so unknown error.
		return cLockError.UE;
	}

	/**
	 * Release a simple lock. Map the call from user request to DLock's release
	 * lock.
	 * 
	 * @param rId
	 *            : Unique Resource Id mentioned by user in the request
	 * @param rState
	 *            : State of the resource mentioned by user in the request that
	 *            the resource is locked with.
	 * @param user
	 *            : Unique user Id mentioned by user in the request
	 * @param lt
	 *            : Lock Type mentioned by user in the request that the resource
	 *            is locked with.
	 */
	public void releaseLock(String rId, String state, String user, LockType lt) {

		DLock dLock = lockList.get(rId);
		if (dLock != null) {
			dLock.releaseLock(state, user, lt);
		}
	}

	/**
	 * Update the lock. Maps the call to update the lock from LockWalker to
	 * DLock'supdateLock.
	 * 
	 * @param rId
	 *            : Unique Resource Id mentioned by user in the request
	 * @param rState
	 *            : State of the resource mentioned by user in the request that
	 *            the resource is locked with.
	 * @param user
	 *            : Unique user Id mentioned by user in the request
	 * @param lt
	 *            : Lock Type mentioned by user in the request that the resource
	 *            is locked with.
	 */
	public void updateLock(String rId, String state, String user, LockType lt) {

		DLock dLock = lockList.get(rId);
		if (dLock != null) {
			dLock.updateLock();
		}
	}

	/**
	 * Update the lock. Maps the call to reset timer of the lock from LockWalker
	 * to DLock'resetLock.
	 * 
	 * @param rId
	 *            : Unique Resource Id mentioned by user in the request
	 * @param rState
	 *            : State of the resource mentioned by user in the request that
	 *            the resource is locked with.
	 * @param user
	 *            : Unique user Id mentioned by user in the request
	 * @param lt
	 *            : Lock Type mentioned by user in the request that the resource
	 *            is locked with.
	 */
	public cLockError resetLock(String rId, String state, String user,
			LockType lt) {

		DLock dLock = lockList.get(rId);
		if (dLock != null) {
			boolean bl = dLock.resetLock(state, user, lt);
			// Return the error in the lock to user.
			if (!bl) {
				return cLockError.DL;
			}
		} else {
			return cLockError.LR;
		}
		return cLockError.NE;
	}

	/**
	 * Delete the Dlock from ServerManager's list of locks. This method is
	 * called by LockWalker when it revokes all the simple locks from a DLock,
	 * the DLock also should be cleared.
	 * 
	 * @param rId
	 *            : The unique resource Id which also identifies the lock that
	 *            the resource holds.
	 */
	public void deleteLock(String rId) {
		lockList.remove(rId);
	}
}
