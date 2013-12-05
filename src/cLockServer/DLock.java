package cLockServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class for a Distributed Lock. Multiple users can lock a resource in
 * same state with same/ different lockType. This container holds the simple
 * locks with the resource Id and state. Also this class maps the basic
 * operations on locks to individual lock.
 * 
 * @author Nachiket
 * 
 */

public class DLock {

	/**
	 * Resource Id.
	 */
	public String rId;

	/**
	 * State in which the resource is locked.
	 */
	public String state;

	/**
	 * List of simple locks on the resource.
	 */
	List<SimpleLock> rLocks;

	/**
	 * Constructor to create the Distributed Lock
	 * 
	 * @param rId
	 *            : unique Id of the resource.
	 * @param state
	 *            : State in which the resource is locked.
	 */
	public DLock(String rId, String state) {

		this.rId = rId;
		this.state = state;
		rLocks = new ArrayList<SimpleLock>();
	}

	/**
	 * Create a simple lock for the resource. If the lock is created, return
	 * true else return false.
	 * 
	 * @param state
	 *            : State in which the user has requested to create a simple
	 *            lock.
	 * @param user
	 *            : User Id who has requested to lock the resource.
	 * @param lt
	 *            : Lock Type.
	 * @return : true is lock can be created, false otherwise.
	 */
	public boolean createLock(String state, String user, LockType lt) {

		// Check if the state user wants to lock the resource is same as the
		// state in which the resource is already locked.
		if (!(this.state.equals(state))) {
			return false;
		} else if (!(getHighestLock().compareTo(LockType.XL) == 0)) {
			// If the resource is not locked in exclusive lock
			if ((lt == LockType.XL)
					&& (getHighestLock().compareTo(LockType.NL) > 0)) {
				// If requested type is exclusive lock and there is another lock
				// with higher priority than null lock already present in the
				// DLock, return false
				return false;
			}
			if (lt.compareTo(getHighestLock()) >= 0) {
				// If requested lockType is severe than current highest lock or
				// equal to current lock, create the simple lock and return true
				SimpleLock sLock = new SimpleLock(user, lt, 0);
				rLocks.add(sLock);
				return true;
			}
		}
		// The state of request and current lock doesn't match.
		return false;
	}

	/**
	 * Query to check whether a lock can be created for given resource with
	 * given state and given lockType. This function works exactly same as
	 * creating simple lock except actual creation opf lock.
	 * 
	 * @param state
	 *            : State in which the user has requested to create a simple
	 *            lock.
	 * @param user
	 *            : User Id who has requested to lock the resource.
	 * @param lt
	 *            : Lock Type.
	 * @return true is lock can be created, false otherwise.
	 */
	public boolean canCreate(String state, String user, LockType lt) {

		// Check if the state user wants to lock the resource is same as the
		// state in which the resource is already locked.
		if (!(this.state.equals(state))) {
			return false;
		} else if (!(getHighestLock().compareTo(LockType.XL) == 0)) {
			// If the resource is not locked in exclusive lock

			if ((lt == LockType.XL)
					&& (getHighestLock().compareTo(LockType.NL) > 0)) {
				// If requested type is exclusive lock and there is another lock
				// with higher priority than null lock already present in the
				// DLock, return false

				return false;
			} else if (lt.compareTo(getHighestLock()) >= 0) {
				// If requested lockType is severe than current highest lock or
				// equal to current lock return true
				return true;
			}
		}
		// The state of request and current lock doesn't match.
		return false;
	}

	/**
	 * Search for the simple lock in the list of simple locks associated with
	 * this Distributed Lock.
	 * 
	 * @param user
	 *            : User Id with which the simple lock should be searched.
	 * @return [int] : Index of the simple lock in the DLock.
	 */
	private int searchLock(String user) {

		int i = -1;

		for (SimpleLock sl : rLocks) {
			i++;
			if (sl.user.equals(user)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Release the simple lock.
	 * 
	 * @param state
	 *            : State in which the user has requested to create a simple
	 *            lock.
	 * @param user
	 *            : User Id who has requested to release the lock on the
	 *            resource.
	 * @param lt
	 *            : Lock Type in which the resoure is locked by user.
	 */
	public void releaseLock(String state, String user, LockType lt) {

		// Search the simple lock in the DLock.
		int index = searchLock(user);

		// remove the lock from the list of simple locks.
		if (index >= 0) {
			rLocks.remove(index);
		}
	}

	/**
	 * Update Lock is used by the Lockwalker to increase the time for which the
	 * lock has been locked. The user resets the time periodically. If the lock
	 * is not reset for particular time, the lockwalker keeps updating the time
	 * and eventually revokes the lock.
	 */
	public void updateLock() {

		// increase the time of the lock.
		for (SimpleLock sl : rLocks) {
			sl.updateTime();
		}
	}

	/**
	 * Reset the timer for a simple lock. User must reset the time of the lock
	 * periodically to maintain the lock.
	 * 
	 * @param state
	 *            : State in which the resource is locked.
	 * @param user
	 *            : User Id who has requested to reset the lock on the resource.
	 *            This user Id is used to search the lock in the list of simple
	 *            locks.
	 * @param lt
	 *            : Lock Type of the current lock.
	 * @return true if reset time was successful else false to return that the
	 *         lock has been revoked.
	 */
	public boolean resetLock(String state, String user, LockType lt) {

		// Search the simple lock with user id in the request.
		int index = searchLock(user);
		if (index >= 0) {
			// simple lock found, reset its time.
			SimpleLock sl = rLocks.get(index);
			sl.resetTime();
			rLocks.set(index, sl);

			// check if deadlock bit is set by LockWalker, if it is set, return
			// false.
			if (sl.deadLock) {
				return false;
			} else {
				return true;
			}
		}

		// Lock not found means the lock has been revoked.
		return false;
	}

	/**
	 * Get the highest priority lock in the list of simple locks associated with
	 * current DLcok. Simple linear search is used across the list of simple
	 * locks to find the highest lock Type.
	 * 
	 * @return : lockType of the highest priority lock.
	 */
	public LockType getHighestLock() {
		if (rLocks.size() > 0) {
			SimpleLock hLock = rLocks.get(0);
			for (SimpleLock l : rLocks) {
				if (hLock.lt.compareTo(l.lt) < 0) {
					hLock = l;
				}
			}
			return hLock.lt;
		}
		return LockType.NL;
	}
}
