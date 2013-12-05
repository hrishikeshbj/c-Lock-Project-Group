package cLockServer;

/**
 * //A container to hold a resource in terms of resource table manager. The
 * Resource table contains only 4 columns resource Id, resource name , creating
 * user and state. These 4 columns are fields of this class.
 * 
 * @author Nachiket
 * 
 */
public class Resource {

	/**
	 * Resource Id.
	 */
	private String rId;

	/**
	 * Resource Name.
	 */
	private String rName;

	/**
	 * Creating user.
	 */
	private String user;

	/**
	 * State.
	 */
	private String rState;

	/**
	 * constructor to create a resource.
	 * 
	 * @param rId
	 *            Resource Id
	 * @param rName
	 *            resource name
	 * @param user
	 *            creating user
	 * @param rState
	 *            state.
	 */
	public Resource(String rId, String rName, String user, String rState) {
		this.rId = rId;
		this.rName = rName;
		this.user = user;
		this.rState = rState;
	}

	/**
	 * Get the resource Id from resource
	 * 
	 * @return : resource Id.
	 */
	public String getrId() {
		return rId;
	}

	/**
	 * Get the resource name from the resource.
	 * 
	 * @return : resource name.
	 */
	public String getrName() {
		return rName;
	}

	/**
	 * Get teh id of creating user.
	 * 
	 * @return : user id.
	 */
	public String getuser() {
		return user;
	}

	/**
	 * get the state from the resource.
	 * 
	 * @return : State.
	 */
	public String getrState() {
		return rState;
	}

	/**
	 *  convert the resource into a string.
	 */
	public String toString() {
		String str = "<" + rId + ", " + rName + ", " + user + ", " + rState
				+ ">";
		return str;
	}
}
