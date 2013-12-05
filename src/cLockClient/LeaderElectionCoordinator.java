package cLockClient;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Module to help leader election. User must set application name for leader
 * election to happen the leader Id is generated with fixed format by the leader
 * election coordinator to avoid any confusions.
 * 
 * @author Nachiket
 * 
 */
public class LeaderElectionCoordinator {

	/**
	 * Name of the application for which the LEC is used.
	 */
	private String appName;

	/**
	 * Default constructor, sets the appname empty.
	 */
	public LeaderElectionCoordinator() {
		appName = null;
	}

	/**
	 * Constructor to initialize the leader Election coordinator
	 * 
	 * @param appName
	 *            : application name used by LEC.
	 */
	public LeaderElectionCoordinator(String appName) {
		this.appName = appName;

	}

	/**
	 * Set application name.
	 * 
	 * @param appName
	 *            : application name.
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * this method checks whether the application is registered with server as a
	 * resource or not. If not, then register the resource and then if the
	 * resource can be locked or not in exclusive lock. If not it is lockable
	 * then lock the resource with exclusive lock as LEADER and return true else
	 * return false.
	 * 
	 * @return true if the application can e leader else false.
	 * @throws LockException
	 *             : if lock fails at local client.
	 * @throws UnknownHostException
	 *             : if the server cannot be found.
	 * 
	 * @throws IOException
	 *             : if communication with server fails.
	 */
	public boolean amITheLeader() throws LockException, UnknownHostException,
			IOException {

		if (this.appName == null) {
			throw new LockException(LockErrorMessage.LERR_NOAN);
		} else {
			Resource rs = cLockClient.resources.get(appName);
			if (rs == null) {
				String state = "LEADER";
				String rId = appName + "LEADER";
				LockType lt = LockType.XL;
				rs = new Resource(appName);
				rs.setRId(rId);
				rs.addState(state);

				// TODO canCreate the lock on server
				if (rs.canLock(lt, state)) {
					return rs.lock(lt, state);
				} else {
					return false;
				}
			} else {
				if (rs.getLockType() == null) {
					if (rs.canLock(LockType.XL, "LEADER")) {
						return rs.lock(LockType.XL, "LEADER");
					} else {
						return false;
					}
				} else {
					rs.update();
					return true;
				}
			}
		}
	}

	/**
	 * Abjugate the leadership. The exclusive lock is released on the server.
	 * 
	 * @throws LockException
	 *             : If lock fails at client itself.
	 * @throws UnknownHostException
	 *             : if server cannot be found.
	 * @throws IOException
	 *             : if communication fails with server.
	 */
	public void relieve() throws LockException, UnknownHostException,
			IOException {

		// if application name is not set the nthrow error.
		if (this.appName == null) {
			throw new LockException(LockErrorMessage.LERR_NOAN);
		} else {
			// release the lock on server.
			Resource rs = cLockClient.resources.get(appName);
			if (rs != null) {
				rs.release();
			}
		}
	}
}
