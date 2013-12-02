package cLockClient;

public class LockErrorMessage {

	public static final String LERR_NOID = "Error: No Id - The resource is not initialized yet. Explicitly set resource Id or retrieve it from c-Lock";
	public static final String LERR_DUPR = "Error: The resource is already created. Resource names must be unique.";
	public static final String LERR_NOLK = "Error: The resource is not locked.";
	public static final String LERR_NOURL = "Error: The client is not yet initialized. Need cLock URL and user id.";
	public static final String LERR_NOAN = "Error: The LeaderElectionCoordinator is not initialized correctly. Need application name.";
}
