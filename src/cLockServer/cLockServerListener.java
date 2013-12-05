package cLockServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Random;

/**
 * Server Listener is a runnable thread. At the beginning of establishing
 * cLockServer, this thread is started. ServerListener listens to Gossip
 * messages from other cLockServers. The servers in the network communicate in
 * asynchronous manner using Sockets. A ServerSocket is created and it
 * continuously listens to any connections from other servers. It accepts
 * messages from other servers, performs appropriate operation and forwards the
 * message based on the server configuration.
 * 
 * @author Nachiket
 * 
 */
public class cLockServerListener implements Runnable {

	public cLockServerListener() {

	}

	/**
	 * The run of the cLockServerListener.
	 */
	@Override
	public void run() {
		try {
			listen();
		} catch (IOException e) {
			// ignore.
		}
	}

	/**
	 * Continuously listen on ServerSocket and accept messages from other
	 * servers, Perform appropriate operation and forward the message as per
	 * server configuration.
	 * 
	 * @throws IOException
	 */
	public void listen() throws IOException {
		// System.out.println("Listening");

		// create server socket to listen on.
		ServerSocket listener = new ServerSocket(9092);
		// Message Parser to parse the message from other servers.
		MessageParser mp = new MessageParser();

		while (true) {
			try {
				// new connection.
				Socket skt = listener.accept();
				DataInputStream in = new DataInputStream(skt.getInputStream());

				// read the message.
				String ln = in.readUTF();
				// parse the message into an instance of Message
				Message msg = mp.parse(ln);
				skt.close();
				doOperation(msg);
				// forward the message
			} catch (IOException | SQLException e) {
				if (!e.getMessage().contains("reset")) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Check the operation mentioned in the message and perform appropriate
	 * operation. There is no synchronous reply in the message passing.
	 * 
	 * @param msg
	 *            : instance of Message that contains all the necessary fields
	 *            and operation.
	 * @throws SQLException
	 *             : If the operation to be performed fails.
	 * @throws UnknownHostException
	 *             : If the message forwarding fails.
	 * @throws IOException
	 *             : If the message forwarding fails
	 */
	public void doOperation(Message msg) throws SQLException,
			UnknownHostException, IOException {

		// If the message traverses back to the originator, ignore the message.
		if (!msg.getDesc().equals(cLockServerManager.self)) {

			// Creating the resource.
			if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_CREATE)) {

				// create the instance of resource
				Resource rs = new Resource(msg.getRId(), msg.getRName(),
						msg.getUser(), msg.getRState());
				// put the resource in the DB.
				cLockServerManager.rtm.createResource(rs);

			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_DELETE)) {

				// Delete the resource from the database.
				cLockServerManager.rtm.deleteResource(msg.getRId());

			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_LOCK)) {

				// lock the mentioned resource with the apopropriate lockType
				// and state mentioned in the message
				cLockServerManager.dlm.createLock(msg.getRId(),
						msg.getRState(), msg.getUser(),
						LockType.getLType(msg.getLType()));

			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_RELEASE)) {

				// Release the lock from the DLM.
				cLockServerManager.dlm.releaseLock(msg.getRId(),
						msg.getRState(), msg.getUser(),
						LockType.getLType(msg.getLType()));

			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_CANLOCK)) {

				// Server is asking can the lock be created on this server.
				// Lock is not actually created just checked whether the lock
				// can
				// be created or not.
				boolean bl = cLockServerManager.dlm.canCreate(msg.getRId(),
						msg.getRState(), msg.getUser(),
						LockType.getLType(msg.getLType()));
				if (bl) {
					msg.setOp(cLockConstants.OP_SUCCESS);
				} else {
					msg.setOp(cLockConstants.OP_FAIL);
				}

				// This is part of quorum.
				// Return the result to the gossip initiator.
				Socket skt = new Socket(msg.getDesc(), 9092);
				DataOutputStream out = new DataOutputStream(
						skt.getOutputStream());
				out.writeUTF(msg.toString());

			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_UPDATE)) {

				// Update the lock (reset its time counter).
				cLockServerManager.dlm.resetLock(msg.getRId(), msg.getRState(),
						msg.getUser(), LockType.getLType(msg.getLType()));

			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_SUCCESS)) {

				// This is a part of quorum
				// The current host has initiated the quorum and other server is
				// replying to canLock request by current server with success.
				// This reply is recorded in quorumCounter for the message.

				QuorumCounter counter = cLockServerManager.qc.get(msg.getMId());
				counter.incPositive();

				// update the quorum counter on the server manager for reply to
				// user.
				cLockServerManager.qc.put(msg.getMId(), counter);

			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_FAIL)) {

				// This is a part of quorum
				// The current host has initiated the quorum and other server is
				// replying to canLock request by current server with failure.
				// This reply is recorded in quorumCounter for the message.
				QuorumCounter counter = cLockServerManager.qc.get(msg.getMId());
				counter.incNegative();

				// update the quorum counter on the server manager for reply to
				// user.
				cLockServerManager.qc.put(msg.getMId(), counter);
			}
			// Forward the message.
			forward(msg);
		}
	}

	/**
	 * Forwarding the message is based on gossip protocol. The properties of
	 * gossip are set reading the configuration file. If the number of adjacent
	 * servers is less than 3 and forwarding is off then the message is dropped
	 * else the message is forwarded with one less nHops and another message is
	 * created with nHops = logarithm of the number of nodes in network.
	 * 
	 * @param msg
	 *            : instance of Message that contains all the necessary fields
	 *            and operation.
	 * @throws UnknownHostException
	 *             : If the message forwarding fails.
	 * @throws IOException
	 *             : If the message forwarding fails.
	 */
	public void forward(Message msg) throws UnknownHostException, IOException {

		int msgNHops = Integer.parseInt(msg.getnHops());

		// Forward the message only if forward is set to true.
		if (cLockServerManager.forward) {

			Random rand = new Random();

			// forward to random server
			int sIndex = rand.nextInt(cLockServerManager.servers.size());

			if (msgNHops > 1) {
				msgNHops = msgNHops - 1;
				msg.setNHops(Integer.toString(msgNHops));
				Socket skt = new Socket(cLockServerManager.servers.get(sIndex),
						9092);
				DataOutputStream out = new DataOutputStream(
						skt.getOutputStream());
				out.writeUTF(msg.toString());

				sIndex = rand.nextInt(cLockServerManager.servers.size());
			}

			// create new forwarding message.
			// set logarithmic nHops
			int nHops = (int) Math.ceil((double) Math
					.log(cLockServerManager.servers.size())
					/ (double) Math.log(2));
			msg.setNHops(Integer.toString(nHops));
			Socket skt1 = new Socket(cLockServerManager.servers.get(sIndex),
					9092);
			DataOutputStream out1 = new DataOutputStream(skt1.getOutputStream());
			out1.writeUTF(msg.toString());
		}

	}
}
