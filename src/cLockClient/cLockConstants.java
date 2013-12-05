package cLockClient;

/**
 * Constants that define the constants of the cLock server. These constants are
 * same for server and client.
 * 
 * @author Nachiket
 * 
 */
public class cLockConstants {

	/**
	 * Application name for cLock.
	 */
	public static final String APPNAME = "c-Lock";

	/**
	 * Table name for the resources.
	 */
	public static final String resourceTableName = "resourcetable";

	/**
	 * Resource Table name column for resource Id.
	 */
	public static final String RID = "RID";

	/**
	 * Resource Table name column for resource name.
	 */
	public static final String RNAME = "RNAME";

	/**
	 * Resource Table name column for user Id.
	 */
	public static final String USER = "USER";

	/**
	 * Resource Table name column for resource state.
	 */
	public static final String STATE = "STATE";

	/**
	 * Operation name used in server- client and server-server communication for
	 * creating a resource.
	 */
	public static final String OP_CREATE = "CREATE";

	/**
	 * Operation name used in server- client and server-server communication for
	 * deleting a resource.
	 */
	public static final String OP_DELETE = "DELETE";

	/**
	 * Operation name used in server- client and server-server communication for
	 * querying a resource.
	 */
	public static final String OP_GET = "GET";

	/**
	 * Operation name used in server- client and server-server communication to
	 * enquire whether a particular resource can be locked or not.
	 */
	public static final String OP_CANLOCK = "CANLOCK";

	/**
	 * Operation name used in server- client and server-server communication for
	 * locking a resource.
	 */
	public static final String OP_LOCK = "LOCK";

	/**
	 * Operation name used in server- client and server-server communication for
	 * releasing a lock on a resource.
	 */
	public static final String OP_RELEASE = "RELEASE";

	/**
	 * Operation name used in server- client and server-server communication for
	 * updating a lock on a resource.
	 */
	public static final String OP_UPDATE = "UPDATE";
	
	/**
	 * Operation name used in server- client and server-server communication for success of an operation.
	 */
	public static final String OP_SUCCESS = "SUCCESS";
	
	/**
	 * Operation name used in server- client and server-server communication for failure of an operation.
	 */
	public static final String OP_FAIL = "FAIL";
}
