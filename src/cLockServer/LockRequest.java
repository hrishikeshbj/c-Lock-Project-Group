package cLockServer;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class to hold a failed request from user to create a lock. This
 * lockRequest is used by LockWalker to find deadlocks. The Lock request maps
 * from user to resource Id. This way the reverse traversal of resource
 * allocation graph is made easier.
 * 
 * @author Nachiket
 * 
 */
public class LockRequest {

	/**
	 * User Id.
	 */
	String user;

	/**
	 * List of resources that the user failed to lock.
	 */
	List<String> rList;

	/**
	 * Constructor to create the Lock Request.
	 * 
	 * @param user
	 *            : the identifier for failed requests is user.
	 */
	public LockRequest(String user) {
		rList = new ArrayList<String>();
		this.user = user;
	}

	/**
	 * Add a resource to list of resources a user fails to lock.
	 * 
	 * @param rId
	 *            : unique id of the resource that the user failed to lock.
	 */
	public void putRequest(String rId) {
		this.rList.add(rId);
	}
}
