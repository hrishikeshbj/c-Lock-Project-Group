package cLockClient;

public class LeaderElectionCoordinator {
	private String appName;

	public LeaderElectionCoordinator() {
		appName = null;
	}

	public LeaderElectionCoordinator(String appName) {
		this.appName = appName;

	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean amITheLeader() throws LockException {

		if (this.appName == null) {
			throw new LockException(LockErrorMessage.LERR_NOAN);
		} else {
			Resource rs = cLockClient.resources.get(appName);
			if(rs == null) {
				String state = "Leader";
				String rId = appName + "LEADER";
				LockType lt = LockType.XL;
				rs = new Resource(rId, appName);
				rs.addState(state);
				
				// TODO canCreate the lock on server
				// if someone else has lock then return false.
				// else
				rs.lock(lt, state);
			}
			return true;
		}
	}
	
	public void relieve() throws LockException {
		if (this.appName == null) {
			throw new LockException(LockErrorMessage.LERR_NOAN);
		} else {
			Resource rs = cLockClient.resources.get(appName);
			if(rs != null) {
				rs.release();
			}
		}
	}
}
