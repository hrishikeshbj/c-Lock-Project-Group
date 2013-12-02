package cLockClient;

import java.util.ArrayList;
import java.util.List;

public class Resource {
	private String rId;
	private String rName;
	private LockType lt;
	private boolean dl = false;
	List<String> rStates = new ArrayList<String>();

	public Resource(String rId, String rName) {
		this.rId = rId;
		this.rName = rName;
		rStates.add("NOSTATE");
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	public Resource(String rId, String rName, List<String> rStates) {
		this.rId = rId;
		this.rName = rName;
		this.rStates = rStates;
		// TODO create the resource on server
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	public Resource(String rName) {
		this.rName = rName;
		rStates.add("NOSTATE");
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	public void setRId(String rId) {
		this.rId = rId;
		// update the resource on Client RList
		cLockClient.resources.put(rName, this);
	}

	public void addState(String rState) throws LockException {

		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			rStates.add(rState);

			// TODO create the state on server.

			cLockClient.resources.put(rName, this);
		}
	}

	public boolean getrId() throws LockException {
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			// TODO retrieve rId from server.
		}
		return true;
	}

	public boolean getrId(String user) throws LockException {
		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			// TODO retrieve rId from server with different user.
		}
		return true;
	}

	public boolean lock(LockType nlt, String rState) throws LockException {

		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {

			if (rId == null) {
				throw new LockException(LockErrorMessage.LERR_NOID);
			}
			if (rState == null) {
				rState = "NOSTATE";
			}

			if (rStates.contains(rState)) {
				// TODO lock on server

				// update the resource on Client RList
				cLockClient.resources.put(rName, this);
				return true;
			}
		}
		return false;
	}

	public boolean release() throws LockException {

		if (cLockClient.serverURL == null || cLockClient.user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			if (rId == null) {
				throw new LockException(LockErrorMessage.LERR_NOID);
			}
			if (lt == null) {
				throw new LockException(LockErrorMessage.LERR_NOLK);
			}
			// TODO release on server.
			lt = null;
			// update the resource on Client RList
			cLockClient.resources.put(rName, this);
		}
		return true;
	}

	public boolean update() {
		if (rId == null) {
			return false;
		}
		if (lt == null) {
			return false;
		}

		if (cLockClient.serverURL == null || cLockClient.user == null) {
			return false;
		} else {

			// TODO update on server.
			// TODO if deadlock set deadlock bit.
			// update the resource on Client RList
			cLockClient.resources.put(rName, this);
		}
		return true;
	}

	public LockError checkLock() {

		if (lt == null) {
			return LockError.LI;
		} else if (this.dl) {
			return LockError.DL;
		}
		return LockError.LK;
	}

}
