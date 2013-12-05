package cLockClient;

/**
 * Error message to be thrown when an error occurs.
 * 
 * @author Nachiket
 * 
 */
public class LockErrorMessage {

	public static final String LERR_NOID = "Error: No Id - The resource is not initialized yet. Explicitly set resource Id or retrieve it from c-Lock";
	public static final String LERR_DUPR = "Error: The resource is already created. Resource names must be unique.";
	public static final String LERR_NONAME = "Error: Input parameters are invalid.";
	public static final String LERR_NOLK = "Error: The resource is not locked.";
	public static final String LERR_LOCK = "Error: The resource is already locked.";
	public static final String LERR_NOURL = "Error: The client is not yet initialized. Need cLock URL and user id.";
	public static final String LERR_NOAN = "Error: The LeaderElectionCoordinator is not initialized correctly. Need application name.";
	public static final String LERR_NOST = "Error: No such state is associated with the resource.";
}
