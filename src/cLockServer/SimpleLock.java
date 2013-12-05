package cLockServer;

/**
 * A container class for simple lock. A resource can be locked by different user
 * simultaneously with different lockTypes. The DLock contains the resource Id
 * and state in which the resource is locked and has a list of this simple locks
 * that contains the lock Type and the user who has locked the resource.
 * Additionally the simple lock also contains the time counter and deadlock bit.
 * 
 * @author Nachiket
 * 
 */
public class SimpleLock {

	/**
	 * User who has locked the resource.
	 */
	public String user;

	/**
	 * LockType od the lock.
	 */
	public LockType lt;

	/**
	 * counter to count the time since the user updated the lock.
	 */
	public int lockTime;

	/**
	 * Deadlock bit is true if the resource is in deadlock.
	 */
	public boolean deadLock;

	/**
	 * construct a simple lock
	 * 
	 * @param usr
	 *            : user whoc has locked the resurce.
	 * @param lt
	 *            : LockType of the lock.
	 * @param lockTime
	 *            : time since a user updated the lock.
	 */
	public SimpleLock(String usr, LockType lt, int lockTime) {
		this.user = usr;
		this.lt = lt;
		this.lockTime = lockTime;
		this.deadLock = false;
	}

	/**
	 * reset the time counter to 0. When a user updates the lock, the counter is
	 * reset.
	 */
	public void resetTime() {
		this.lockTime = 0;
	}

	/**
	 * Increase the time counter. The lockwalker updates the time of all locks
	 * during its run.
	 */
	public void updateTime() {
		this.lockTime++;
	}

	/**
	 * Id deadlock is found by the LockWalker, it sets the deadlock bit of the
	 * simple lock to true.
	 */
	public void setDeadLock() {
		this.deadLock = true;
	}
}
