package cLockClient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * A container for resource at cLock client. A resource contains resource Id ,
 * resource name and states associated with the resource. If the resource is
 * locked, its lockType is populated with appropriate lock type. After updating
 * lock with server, if there is deadlock for the resource, the deadlock bit is
 * set to true.
 * 
 * @author Nachiket
 * 
 */
public class Resource {

	/**
	 * Resource Id can be set by user, can be generated or queried from server.
	 */
	private String rId;

	/**
	 * Name of the resource must be set by the user.
	 */
	private String rName;

	/**
	 * Lock Type of current lock on the resource. It is null i there is no ock
	 * on the resource.
	 */
	private LockType lt;

	/**
	 * Deadlock bit.
	 */
	private boolean dl = false;

	/**
	 * List of states associated with the resource.
	 */
	List<String> rStates = new ArrayList<String>();

	/**
	 * The resource will be immediately created on cLock server with default
	 * state "NOSTATE".
	 * 
	 * @param rId
	 *            : Unique resource Id.
	 * @param rName
	 *            : resource name.
	 * @throws LockException
	 *             : when a resource exists with same name as the resource to be
	 *             created.
	 * @throws UnknownHostException
	 *             : when server cannot be reached.
	 * @throws IOException
	 *             : when communication with server fails.
	 */
	public Resource(String rId, String rName) throws LockException,
			UnknownHostException, IOException {
		if (rId == null || rName == null) {
			throw new LockException(LockErrorMessage.LERR_NONAME);
		}
		this.rId = rId;
		this.rName = rName;
		this.rStates.add("NOSTATE");
		this.lt = null;
		if (cLockClient.resources.get(rName) == null) {
			// update the resource on Client RList
			cLockClient.resources.put(rName, this);

			// create the resource on server with state NOSTATE.
			// This state is used for resources with no state mentioned.
			Message msg = new Message();
			msg.setMId("usermsg" + System.currentTimeMillis());
			msg.setNHops("0");
			msg.setRName(this.rName);
			msg.setRId(this.rId);
			msg.setRState("NOSTATE");
			msg.setOp(cLockConstants.OP_CREATE);
			msg.setUser(cLockClient.user);
			msg.setLType(LockType.NL.toString());
			msg.setDesc("fromuser");
			cLockClient.doOperation(msg);
		} else {
			// the resource tried to create is duplicate of a resource already
			// present.
			throw new LockException(LockErrorMessage.LERR_DUPR);
		}

	}

	/**
	 * This constructor must be used for resources that are already present on
	 * cLock server. This resource will not be created on server in this
	 * constructor.
	 * 
	 * @param rId
	 *            : resource id present on cLock server
	 * @param rName
	 *            : name of the resource
	 * @param rStates
	 *            : List of states present on cLock server.
	 */
	public Resource(String rId, String rName, List<String> rStates) {
		this.rId = rId;
		this.rName = rName;
		this.rStates = rStates;
		this.lt = null;
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	/**
	 * With this constructor, the resource will not be created immediately on
	 * server. But to create resource on server, Id must be generated and a
	 * state must be mentioned.
	 * 
	 * @param rName
	 *            : name of the resource.
	 */
	public Resource(String rName) {
		this.rName = rName;
		this.lt = null;
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	/**
	 * Get the resource name from the resource
	 * 
	 * @return resource name of the resource.
	 */
	public String getRName() {
		return this.rName;
	}

	/**
	 * get the resource Id of this resource.
	 * 
	 * @return : Resource Id.
	 */
	public String getRId() {
		return this.rId;
	}

	/**
	 * check whether the deadlock bit is set for the resource or not.
	 * 
	 * @return true if deadlock bit is set else false.
	 */
	public boolean isDeadLock() {
		return this.dl;
	}

	/**
	 * get the type of lock the resource is currently locked in.
	 * 
	 * @return : lock Type of the resource.
	 */
	public LockType getLockType() {
		return this.lt;
	}

	/**
	 * Generate unique resource Id for the resource. Resource is not immediately
	 * created on cLock server.
	 */
	public void generateRId() {
		this.rId = new String(rName + System.currentTimeMillis());
		this.lt = null;
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	/**
	 * set resource Id for the resource. The Resource is not immediately created
	 * on cLock server.
	 * 
	 * @param rId
	 *            : unique resource Id.
	 */
	public void setRId(String rId) {
		this.rId = rId;
		this.lt = null;
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	/**
	 * Add state to the resource. The state is immediately updated on server.
	 * 
	 * @throws IOException
	 *             : when communication with server fails.
	 * @throws UnknownHostException
	 *             : : when server cannot be reached.
	 */
	public void addState(String rState) throws LockException,
			UnknownHostException, IOException {

		// throw exception that client is not initialized if server url or user
		// id are null.
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else if (rState == null) {
			// if no state is mentioned in the call, throw exception.
			throw new LockException(LockErrorMessage.LERR_NONAME);
		} else {

			// create the state only if the list of states doesn't contain the
			// state.
			if (!rStates.contains(rState)) {
				rStates.add(rState);
				cLockClient.resources.put(rName, this);

				// create the resource on server with state NOSTATE.
				// This state is used for resources with no state mentioned.
				Message msg = new Message();
				msg.setMId("usermsg" + System.currentTimeMillis());
				msg.setNHops("0");
				msg.setRName(this.rName);
				msg.setRId(this.rId);
				msg.setRState(rState);
				msg.setOp(cLockConstants.OP_CREATE);
				msg.setUser(cLockClient.user);
				msg.setLType(LockType.NL.toString());
				msg.setDesc("fromuser");
				cLockClient.doOperation(msg);
			}
		}
	}

	/**
	 * Add state to the resource. The state is not updated on server.
	 * 
	 * @throws IOException
	 *             : if the communication with server fails.
	 * @throws UnknownHostException
	 *             : if the server cannot be reached.
	 */
	public void addState(String rState, boolean val) throws LockException,
			UnknownHostException, IOException {

		// state must be provided in the parameters
		if (rState == null) {
			throw new LockException(LockErrorMessage.LERR_NONAME);
		} else {

			if (!rStates.contains(rState)) {
				rStates.add(rState);
				cLockClient.resources.put(rName, this);
			}
		}
	}

	/**
	 * Retrieve resource unique Id from cLock server. Default user od
	 * cLockClient is used to retrieve the resource Id.
	 * 
	 * @return resourceId retrieved from the server
	 * @throws LockException
	 *             : if the client is not initialized properly.
	 * @throws IOException
	 *             : if communication with server fails.
	 * @throws UnknownHostException
	 *             : if server is not reachable.
	 */
	public String retrieveRId() throws LockException, UnknownHostException,
			IOException {

		this.lt = null;

		// throw exception that client is not initialized if server url or user
		// id are null.
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			// retrieve rId from server.
			Message msg = new Message();
			msg.setMId("usermsg" + System.currentTimeMillis());
			msg.setNHops("0");
			msg.setRName(this.rName);
			msg.setRId("NONE");
			msg.setRState("NONE");
			msg.setOp(cLockConstants.OP_GET);
			msg.setUser(cLockClient.user);
			msg.setLType(LockType.NL.toString());
			msg.setDesc("fromuser");

			Message retmsg = cLockClient.doOperation(msg);

			// extract the resource Id from the returned message.
			if (retmsg.getRId().equalsIgnoreCase("null")) {
				return null;
			} else {
				this.rId = retmsg.getRId();
				return this.rId;
			}
		}
	}

	/**
	 * Retrieve the resource unique Id from server. user Id mentioned in
	 * parameters is used to retrieve the resource Id.
	 * 
	 * @param user
	 *            : the user ID to be used to query the resource Id from server.
	 * @return : resource Id retrieved from the server.
	 * @throws LockException
	 *             : if the client is not initialized properly.
	 * @throws IOException
	 *             : if communication with server fails.
	 * @throws UnknownHostException
	 *             : if server is not reachable.
	 */
	public String retrieveRId(String user) throws LockException,
			UnknownHostException, IOException {

		this.lt = null;

		// throw exception that client is not initialized if server url or user
		// id are null.
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else if (user == null) {
			throw new LockException(LockErrorMessage.LERR_NONAME);
		} else {
			// retrieve rId from server with different user.
			Message msg = new Message();
			msg.setMId("usermsg" + System.currentTimeMillis());
			msg.setNHops("0");
			msg.setRName(this.rName);
			msg.setRId("NONE");
			msg.setRState("NONE");
			msg.setOp(cLockConstants.OP_GET);
			msg.setUser(user);
			msg.setLType(LockType.NL.toString());
			msg.setDesc("fromuser");
			Message retmsg = cLockClient.doOperation(msg);

			// extract the resource Id from the returned message.
			if (retmsg.getRId().equalsIgnoreCase("null")) {
				return null;
			} else {
				this.rId = retmsg.getRId();
				return this.rId;
			}
		}

	}

	/**
	 * Delete the resource on the server.
	 * 
	 ** @throws LockException
	 *             : if the client is not initialized properly.
	 * @throws IOException
	 *             : if communication with server fails.
	 * @throws UnknownHostException
	 *             : if server is not reachable.
	 */
	public void delete() throws LockException, UnknownHostException,
			IOException {

		// throw exception that client is not initialized if server url or user
		// id are null.
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			// delete the resource on cLock server.
			Message msg = new Message();
			msg.setMId("usermsg" + System.currentTimeMillis());
			msg.setNHops("0");
			msg.setRName(this.rName);
			msg.setRId(this.rId);
			msg.setRState("NONE");
			msg.setOp(cLockConstants.OP_DELETE);
			msg.setUser(cLockClient.user);
			msg.setLType(LockType.NL.toString());
			msg.setDesc("fromuser");
			cLockClient.doOperation(msg);
		}

	}

	/**
	 * Lock the resource on the server with the given lockType and in given
	 * state.
	 * 
	 * @param nlt
	 *            : Lock type the resource is to be locked with.
	 * @param rState
	 *            : state the resource is to be locked with.
	 * @throws LockException
	 *             : if the client is not initialized properly.
	 * @throws IOException
	 *             : if communication with server fails.
	 * @throws UnknownHostException
	 *             : if server is not reachable.
	 */
	public boolean lock(LockType nlt, String rState) throws LockException,
			UnknownHostException, IOException {

		// throw exception that client is not initialized if server url or user
		// id are null.
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);

		} else if (nlt == null) {

			// Lock Type must not be null in parameter
			throw new LockException(LockErrorMessage.LERR_NONAME);
		} else if (this.lt != null) {

			// The resource must not be locked in some lockType.
			throw new LockException(LockErrorMessage.LERR_LOCK);
		} else {

			// if resource is not initiated correctly.
			if (this.rId == null) {
				throw new LockException(LockErrorMessage.LERR_NOID);
			}

			// use default state if no state is mentioned in the call.
			if (rState == null) {
				rState = "NOSTATE";
			}

			if (rStates.contains(rState)) {
				// lock on server
				Message msg = new Message();
				msg.setMId("usermsg" + System.currentTimeMillis());
				msg.setNHops("0");
				msg.setRName(this.rName);
				msg.setRId(this.rId);
				msg.setRState(rState);
				msg.setOp(cLockConstants.OP_LOCK);
				msg.setUser(cLockClient.user);
				msg.setLType(nlt.toString());
				msg.setDesc("fromuser");
				Message retmsg = cLockClient.doOperation(msg);

				if (retmsg.getOp().equals(cLockConstants.OP_SUCCESS)) {
					this.lt = nlt;
					// update the resource on Client RList
					cLockClient.resources.put(rName, this);
					return true;
				} else {
					return false;
				}

			} else {
				// if the state mentioned in request is not registered with
				// client/ server.
				throw new LockException(LockErrorMessage.LERR_NOST);
			}
		}
	}

	/**
	 * Enquire to lock the resource on the server with the given lockType and in
	 * given state.
	 * 
	 * @param nlt
	 *            : Lock type the resource is to be locked with.
	 * @param rState
	 *            : state the resource is to be locked with.
	 * @throws LockException
	 *             : if the client is not initialized properly.
	 * @throws IOException
	 *             : if communication with server fails.
	 * @throws UnknownHostException
	 *             : if server is not reachable.
	 */
	public boolean canLock(LockType nlt, String rState) throws LockException,
			UnknownHostException, IOException {

		// throw exception that client is not initialized if server url or user
		// id are null.
		if (cLockClient.serverURL == null || cLockClient.user == null) {

			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else if (nlt == null) {

			// Lock Type must not be null in parameter
			throw new LockException(LockErrorMessage.LERR_NONAME);
		} else if (this.lt != null) {

			// The resource must not be locked in some lockType.
			throw new LockException(LockErrorMessage.LERR_LOCK);
		} else {

			// if resource is not initiated correctly.
			if (this.rId == null) {
				throw new LockException(LockErrorMessage.LERR_NOID);
			}

			if (rStates.contains(rState)) {
				// lock on server
				Message msg = new Message();
				msg.setMId("usermsg" + System.currentTimeMillis());
				msg.setNHops("0");
				msg.setRName(this.rName);
				msg.setRId(this.rId);
				msg.setRState(rState);
				msg.setOp(cLockConstants.OP_CANLOCK);
				msg.setUser(cLockClient.user);
				msg.setLType(nlt.toString());
				msg.setDesc("fromuser");
				Message retmsg = cLockClient.doOperation(msg);

				if (retmsg.getOp().equals(cLockConstants.OP_SUCCESS)) {
					return true;
				} else {
					return false;
				}

			} else {
				// if the state mentioned in request is not registered with
				// client/ server.
				throw new LockException(LockErrorMessage.LERR_NOST);
			}
		}
	}

	/**
	 * Release the lock on the server.
	 * 
	 * @throws LockException
	 *             : if the client is not initialized properly.
	 * @throws IOException
	 *             : if communication with server fails.
	 * @throws UnknownHostException
	 *             : if server is not reachable.
	 */
	public boolean release() throws LockException, UnknownHostException,
			IOException {

		// throw exception that client is not initialized if server url or user
		// id are null.
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);

		} else {

			if (rId == null) {
				// Resource must be initiaized correctly.
				throw new LockException(LockErrorMessage.LERR_NOID);
			}
			if (lt == null) {
				// If th resource is not locked, it's cannot be released.
				throw new LockException(LockErrorMessage.LERR_NOLK);
			}

			// release on server.
			Message msg = new Message();
			msg.setMId("usermsg" + System.currentTimeMillis());
			msg.setNHops("0");
			msg.setRName(this.rName);
			msg.setRId(this.rId);
			msg.setRState("NOSTATE");
			msg.setOp(cLockConstants.OP_RELEASE);
			msg.setUser(cLockClient.user);
			msg.setLType(this.lt.toString());
			msg.setDesc("fromuser");
			cLockClient.doOperation(msg);

			this.lt = null;
			// update the resource on Client RList
			cLockClient.resources.put(rName, this);
		}
		return true;
	}

	/**
	 * Update the lock on server to maintain the lock. This resets the time
	 * counter for the lock on server. If returned false, user must check the
	 * resource to find out what is the problem.
	 * 
	 * @return : true if update successful else false
	 * 
	 * @throws LockException
	 *             : if the client is not initialized properly.
	 * @throws IOException
	 *             : if communication with server fails.
	 * @throws UnknownHostException
	 *             : if server is not reachable.
	 */
	public boolean update() throws UnknownHostException, LockException,
			IOException {
		if (rId == null) {
			return false;
		}
		if (lt == null) {
			return false;
		}

		if (cLockClient.serverURL == null || cLockClient.user == null) {
			return false;
		} else {

			// update on server.
			Message msg = new Message();
			msg.setMId("usermsg" + System.currentTimeMillis());
			msg.setNHops("0");
			msg.setRName(this.rName);
			msg.setRId(this.rId);
			msg.setRState("NOSTATE");
			msg.setOp(cLockConstants.OP_UPDATE);
			msg.setUser(cLockClient.user);
			msg.setLType(this.lt.toString());
			msg.setDesc("fromuser");
			Message retmsg = cLockClient.doOperation(msg);
			// if deadlock set deadlock bit.
			if (retmsg.getOp().equals(cLockConstants.OP_FAIL)) {
				this.lt = null;
			} else {
				if (retmsg.getDesc().equalsIgnoreCase("DL")) {
					this.dl = true;
				}
			}
			// update the resource on Client RList
			cLockClient.resources.put(rName, this);
		}
		return true;
	}

	/**
	 * check the condition of the lock. user should periodically chec kthe
	 * condition of the lock. It returns the lock Error based on the condition
	 * of the lock.
	 * 
	 * @return : LockError - see LockError.
	 */
	public LockError checkLock() {

		// if there is no lock present, return LockInvoked.
		if (lt == null) {
			return LockError.LI;

		} else if (this.dl) {
			// If deadLock bit is set, return deadlock error.
			return LockError.DL;
		}

		// else return lock intact.
		return LockError.LK;
	}

}
