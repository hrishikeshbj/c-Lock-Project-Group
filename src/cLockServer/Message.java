package cLockServer;

/**
 * A container to hold a message from user to server or between servers. Message
 * is the basic unit of communication between the client - server and server -
 * server.
 * 
 * @author Nachiket
 * 
 */
public class Message {
	/**
	 * Unique message Id.
	 */
	private String mId;

	/**
	 * number of Hops that message should take before it is dropped.
	 */
	private String nHops;

	/**
	 * Unique id of resource that the operation must be performed.
	 */
	private String rId;

	/**
	 * Name of the resource that the operation must be performed.
	 */
	private String rName;

	/**
	 * State of the resource in contention.
	 */
	private String rState;

	/**
	 * Operation to be performe don he resource.
	 */
	private String op;

	/**
	 * LockType in which the lock must be created or released or updated.
	 */
	private String lType;

	/**
	 * Description of message. for client-server communication, this description
	 * contains errors. for server - server communication, this description
	 * contains address of gossip initiating server.
	 */
	private String desc;

	/**
	 * Unique user Id.
	 */
	private String user;

	public Message() {

	}

	/**
	 * Constructor to construct a message with all the fields.
	 * 
	 * @param mId
	 *            : Unique message Id.
	 * @param nHops
	 *            : number of Hops that message should take before it is
	 *            dropped.
	 * @param rId
	 *            : Unique id of resource that the operation must be performed.
	 * @param rName
	 *            : Name of the resource that the operation must be performed.
	 * @param rState
	 *            : State of the resource in contention.
	 * @param op
	 *            : Operation to be performe don he resource.
	 * @param lType
	 *            : LockType in which the lock must be created or released or
	 *            updated.
	 * @param desc
	 *            : description of the message.
	 * @param user
	 *            : Unique user Id.
	 */
	public Message(String mId, String nHops, String rId, String rName,
			String rState, String op, String lType, String desc, String user) {
		this.mId = mId;
		this.nHops = nHops;
		this.rId = rId;
		this.rName = rName;
		this.rState = rState;
		this.op = op;
		this.lType = lType;
		this.desc = desc;
		this.user = user;
	}

	/**
	 * Set message id in the message
	 * 
	 * @param mId
	 *            :Unique message IdUnique message Id
	 */
	public void setMId(String mId) {
		this.mId = mId;
	}

	/**
	 * Get the message Id from the message.
	 * 
	 * @return : message id.
	 */
	public String getMId() {
		return this.mId;
	}

	/**
	 * Set the number of hops in the message
	 * 
	 * @param nHops
	 *            : number of hops.
	 */
	public void setNHops(String nHops) {
		this.nHops = nHops;
	}

	/**
	 * get the number of hops from the message.
	 * 
	 * @return : number of hops.
	 */
	public String getnHops() {
		return this.nHops;
	}

	/**
	 * Set the unique resource Id
	 * 
	 * @param rId
	 *            : resource Id.
	 */
	public void setRId(String rId) {
		this.rId = rId;
	}

	/**
	 * get the resource Id from the message.
	 * 
	 * @return resource Id
	 */
	public String getRId() {
		return this.rId;
	}

	/**
	 * set the resource name in the message
	 * 
	 * @param rName
	 *            : resource name.
	 */
	public void setRName(String rName) {
		this.rName = rName;
	}

	/**
	 * get teh resource name from the message
	 * 
	 * @return : resource name.
	 */
	public String getRName() {
		return this.rName;
	}

	/**
	 * Set the resource state in teh message
	 * 
	 * @param rState
	 *            : state of the resource.
	 */
	public void setRState(String rState) {
		this.rState = rState;
	}

	/**
	 * get the state of the resource from the message.
	 * 
	 * @return : state of the resource in message.
	 */
	public String getRState() {
		return this.rState;
	}

	/**
	 * Set the operation to be performed on the resource.
	 * 
	 * @param op
	 *            : Operation code. see class cLockConstants
	 */
	public void setOp(String op) {
		this.op = op;
	}

	/**
	 * Get the operation code in the message.
	 * 
	 * @return : Operation code - see class cLockConstants
	 */
	public String getOp() {
		return this.op;
	}

	/**
	 * Set the lock Type of in the message
	 * 
	 * @param lType
	 *            LockType - see class LockType.
	 */
	public void setLType(String lType) {
		this.lType = lType;
	}

	/**
	 * Get the lock type from the message
	 * 
	 * @return LockType - see class LockType
	 */
	public String getLType() {
		return this.lType;
	}

	/**
	 * Set the description ofmesage i it.
	 * 
	 * @param desc
	 *            : Description
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * get the description from message.
	 * 
	 * @return description
	 */
	public String getDesc() {
		return this.desc;
	}

	/**
	 * Set the user Id in the message.
	 * 
	 * @param user
	 *            : user Id.
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get teh user Id from the message
	 * 
	 * @return : user id.
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * marshal the fields of the Message in a string format. Each field of
	 * message is separated by '|'
	 * 
	 * @return all fields of Message marshaled in a string.
	 */
	public String toString() {
		String str = mId + "|" + nHops + "|" + rId + "|" + rName + "|" + rState
				+ "|" + op + "|" + lType + "|" + desc + "|" + user;
		return str;
	}
}
