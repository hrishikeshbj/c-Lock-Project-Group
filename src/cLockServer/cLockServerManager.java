package cLockServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class is the cLock-Engine. This class is singleton type. It contains
 * static references to ResourceTable Manager and DLock Manager. It also
 * contains static refernces to List of failed requests used by Lock Walker to
 * find deadlocks, other server URLs and configuration. Tis class is responsible
 * for performing appropriate operation based on the user message and then
 * initiating gossip for the operation.
 * 
 * @author Nachiket
 * 
 */
public class cLockServerManager {

	/**
	 * Resource Table Manager handles the database of resources and associated
	 * states.
	 */
	public final static ResourceTableManager rtm = new ResourceTableManager();

	/**
	 * The manager of locks in the memory.
	 */
	public final static DLockManager dlm = new DLockManager(rtm);

	/**
	 * Map that holds the rejected requests with user Id as key.
	 */
	public static Map<String, LockRequest> requests = new HashMap<String, LockRequest>();

	/**
	 * The container for quorum counters. One quorumcounter for one quorum
	 * request (Operation: Lock)
	 */
	public static Map<String, QuorumCounter> qc = new HashMap<String, QuorumCounter>();

	/**
	 * List of URLs of connected servers.
	 */
	public static List<String> servers = new ArrayList<String>();

	/**
	 * Self URL
	 */
	public static String self;

	/**
	 * Bit to decide whether to forward the message or not.
	 */
	public static boolean forward;

	/**
	 * Thread pool for LockWalker and cLock Server Listener threads.
	 */
	public static final ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(
			2);

	cLockServerManager() {
	}

	/**
	 * Initialize the Resource Table Manager and configuration variables. The
	 * configuration variable are read from config file in the workspace.
	 * 
	 * @throws SQLException
	 *             : when table setup fails
	 * @throws IOException
	 *             : when reading the confg fails.
	 */
	public static void setup() throws SQLException, IOException {
		// setup resource table manager.
		rtm.setupTable();

		// Start the lockWalker
		cLockWalker lw = new cLockWalker();
		cLockServerManager.stpe
				.scheduleAtFixedRate(lw, 0, 20, TimeUnit.SECONDS);

		// start the ServerListener
		cLockServerListener sl = new cLockServerListener();
		stpe.execute(sl);

		BufferedReader br = new BufferedReader(new FileReader("config.txt"));
		String ln;
		while ((ln = br.readLine()) != null) {

			StringTokenizer tokenizer = new StringTokenizer(ln, "=");
			String str = tokenizer.nextToken();

			if (str.equalsIgnoreCase("self")) {

				String val = tokenizer.nextToken();
				cLockServerManager.self = val;
			} else if (str.equalsIgnoreCase("server")) {

				String val = tokenizer.nextToken();
				cLockServerManager.servers.add(val);
			} else if (str.equalsIgnoreCase("forward")) {

				String val = tokenizer.nextToken();
				if (val.equalsIgnoreCase("ON")) {
					cLockServerManager.forward = true;
				} else {
					cLockServerManager.forward = false;
				}
			}
		}
	}

	/**
	 * Perform the operation based on the message. and start gossip as needed.
	 * Supported operations are: Resource: CREATE, QUERY, DELETE LOCK : LOCK,
	 * RELEASE, CANLOCK
	 * 
	 * @param msg
	 *            : Instance of Message that contains the operation and the ids
	 *            of resource and user.
	 * @return msg : After performing appropriate operation, the message is
	 *         modified based on outcome of the operation and returned back to
	 *         user.
	 * @throws IOException
	 *             : when forwarding message fails.
	 * @throws InterruptedException
	 *             : when thread is interrupted by system.
	 */
	public static Message doOperation(Message msg) throws IOException,
			InterruptedException {

		// System.out.println(msg.toString());

		Message retmsg = new Message();
		try {

			// Create Resource
			if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_CREATE)) {

				Resource rs = new Resource(msg.getRId(), msg.getRName(),
						msg.getUser(), msg.getRState());
				rtm.createResource(rs);
				// forward it
				cLockServerManager.startGossip(msg);
				msg.setOp(cLockConstants.OP_SUCCESS);
				return msg;

				// Resource Query
			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_GET)) {

				String rId = rtm.queryResource(msg.getRName(), msg.getUser());
				msg.setRId(rId);
				return msg;

				// Resource Delete
			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_DELETE)) {

				rtm.deleteResource(msg.getRId());
				// forward it.
				cLockServerManager.startGossip(msg);
				msg.setOp(cLockConstants.OP_SUCCESS);
				return msg;

				// Lock the resource
			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_LOCK)) {

				// create the lock locally
				cLockError le = dlm.createLock(msg.getRId(), msg.getRState(),
						msg.getUser(), LockType.getLType(msg.getLType()));

				if (le.equals(cLockError.NE)) {
					// Forward Quorum!!!
					msg.setOp(cLockConstants.OP_CANLOCK);

					// For the Lock operation, the quorum is handled in start
					// Gossip
					if (cLockServerManager.startGossip(msg)) {

						msg.setOp(cLockConstants.OP_LOCK);
						cLockServerManager.startGossip(msg);
						msg.setOp(cLockConstants.OP_SUCCESS);
						return msg;

					} else {

						msg.setOp(cLockConstants.OP_FAIL);
						return msg;
					}
				} else {
					// add the request to the list of failed requests.

					LockRequest lr = cLockServerManager.requests.get(msg
							.getUser());
					if (lr == null) {

						lr = new LockRequest(msg.getUser());
						lr.rList.add(msg.getRId());

					} else {
						lr.rList.add(msg.getRId());
					}

					cLockServerManager.requests.put(msg.getUser(), lr);
					msg.setOp(cLockConstants.OP_FAIL);
					return msg;
				}

				// Release the lock.
			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_RELEASE)) {

				dlm.releaseLock(msg.getRId(), msg.getRState(), msg.getUser(),
						LockType.getLType(msg.getLType()));
				// forward
				cLockServerManager.startGossip(msg);
				msg.setOp(cLockConstants.OP_SUCCESS);
				return msg;

				// Can the resource be locked.
			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_CANLOCK)) {
				boolean bl = dlm.canCreate(msg.getRId(), msg.getRState(),
						msg.getUser(), LockType.getLType(msg.getLType()));
				if (bl) {
					msg.setOp(cLockConstants.OP_SUCCESS);
					return msg;
				} else {
					msg.setOp(cLockConstants.OP_FAIL);
					return msg;
				}

				// Update the lock - reset lock timer so that the lock is not
				// revoked by server.
			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_UPDATE)) {

				cLockError le = dlm.resetLock(msg.getRId(), msg.getRState(),
						msg.getUser(), LockType.getLType(msg.getLType()));
				if (le == cLockError.NE) {

					// forward the message to other servers
					cLockServerManager.startGossip(msg);
					msg.setOp(cLockConstants.OP_SUCCESS);
					return msg;

				} else if (le == cLockError.LR) {

					// no need to forward the message as it is revoked
					// eventually other servers will also revoke the lock.
					msg.setOp(cLockConstants.OP_FAIL);
					return msg;

				} else if (le == cLockError.DL) {

					// forward the update message
					// after deadlock detection, the action to be taken is
					// decided by user not by cLock.
					// we forward the message to other servers to update the
					// lock and return Success with DeadLock bit.
					cLockServerManager.startGossip(msg);
					msg.setOp(cLockConstants.OP_SUCCESS);
					msg.setDesc("DL");
					return msg;
				}
			}
		} catch (SQLException e) {
			// TODO Log it.
		}

		return retmsg;
	}

	/**
	 * Add the failed request to the list of failed requests. This list is used
	 * by LockWalker to find Deadlocks.
	 * 
	 * @param rId
	 *            : Resource Id.
	 * @param state
	 *            : State to lock the resource in.
	 * @param user
	 *            : User Id.
	 * @param lt
	 *            : lockType
	 */
	public void addRequest(String rId, String state, String user, LockType lt) {

		// search for request in list of current failed requests.
		LockRequest lr = requests.get(user);
		if (lr == null) {

			// create the request
			lr = new LockRequest(user);
			lr.putRequest(rId);
			requests.put(user, lr);
		} else {
			lr.putRequest(rId);
		}
	}

	/**
	 * Initiate the gossip for a message from user For all operation other than
	 * lock the gossip is just forwarding the message. For Lock, the quorum is
	 * needed. Gossip is asynchronous communicatio protocol. Hence this thread
	 * waits for other servers to respond.
	 * 
	 * @param msg
	 *            : Message that is to be forwarded and that contains all the
	 *            information of operation.
	 * @return : true : If the lock can be granted, false : if the lock cannot
	 *         be granted
	 * @throws IOException
	 *             : If the communication with other servers fails.
	 * @throws InterruptedException
	 *             : If the thread is interrupted by the system.
	 */
	public static boolean startGossip(Message msg) throws IOException,
			InterruptedException {

		msg.setDesc(cLockServerManager.self);
		// add the msg to quorum
		if (msg.getOp().equals(cLockConstants.OP_CANLOCK)) {
			qc.put(msg.getMId(), new QuorumCounter());
		}

		// If there are only 2 other servers, Gossip is not neded, message can be
		// Broadcast with Hops = 1.
		if (cLockServerManager.servers.size() < 3) {
			msg.setNHops("1");
			
			// Broadcast the message to both connected servers. 
			for (String server : cLockServerManager.servers) {
				Socket skt = new Socket(server, 9092);
				DataOutputStream out = new DataOutputStream(
						skt.getOutputStream());
				out.writeUTF(msg.toString());
			}
		} else {
			// set logarithmic nHops. This way we control the number of messages in the system.
			// Messages are limited to nlgn.
			int nHops = (int) Math.ceil((double) Math
					.log(cLockServerManager.servers.size())
					/ (double) Math.log(2));
			msg.setNHops(Integer.toString(nHops));

			// connect to random server.
			Random rand = new Random();
			int sIndex = rand.nextInt(cLockServerManager.servers.size());

			Socket skt = new Socket(cLockServerManager.servers.get(sIndex),
					9092);
			DataOutputStream out = new DataOutputStream(skt.getOutputStream());
			out.writeUTF(msg.toString());

			// connect to other random server.
			int sIndex1;
			do {
				sIndex1 = rand.nextInt(cLockServerManager.servers.size());
			} while (sIndex == sIndex1);

			Socket skt1 = new Socket(cLockServerManager.servers.get(sIndex),
					9092);
			DataOutputStream out1 = new DataOutputStream(skt1.getOutputStream());
			out1.writeUTF(msg.toString());
		}

		// wait for quorum.
		if (msg.getOp().equals(cLockConstants.OP_CANLOCK)) {
			for (int i = 0; i < 50; i++) {
				Thread.sleep(10);
				QuorumCounter counter = qc.get(msg.getMId());
				if (counter.getcounter() > cLockServerManager.servers.size()/2) {
					return counter.getDecision();
				}
			}
		}
		return true;
	}
}
