package cLockClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class for cLock Client. This class holds all the containers needed
 * for the client to work It has list of local reources and states associated
 * with them, server URL and user name. The doOperation of this class carries
 * out the communication between client and server.
 * 
 * @author Nachiket
 * 
 */
public class cLockClient {

	/**
	 * A List of local resources. The requests are checked against ocal resource
	 * before communicating with server.
	 */
	public static Map<String, Resource> resources = new HashMap<String, Resource>();

	/**
	 * Scheduled thread pool to periodically send heart beat to server.
	 */
	final ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);

	/**
	 * Runnable thread of heart beat.
	 */
	final HeartBeat hb = new HeartBeat();

	/**
	 * Message parser to parse the message.
	 */
	private static MessageParser mp = new MessageParser();

	/**
	 * Server URL must be set by the user before communicating with server.
	 */
	public static String serverURL;

	/***
	 * User Id must be set by the user before communicating with server.
	 */
	public static String user;

	/**
	 * Default constructor, initialize the server url and user id empty. And
	 * start the heart beating thread.
	 */
	public cLockClient() {

		serverURL = null;
		user = null;
		stpe.scheduleAtFixedRate(hb, 0, 20, TimeUnit.SECONDS);
	}

	/**
	 * Constructor, initialize the server url and user id with given parameters.
	 * And start the heart beating thread.
	 * 
	 * @param serverURL
	 *            : address of the server
	 * @param user
	 *            : user Id.
	 */
	public cLockClient(String serverURL, String user) {
		cLockClient.serverURL = serverURL;
		cLockClient.user = user;
		stpe.scheduleAtFixedRate(hb, 0, 20, TimeUnit.SECONDS);
	}

	/**
	 * Set the server URl for the client.
	 * 
	 * @param URL
	 *            : server address.
	 */
	public void setServerURl(String URL) {
		cLockClient.serverURL = URL;
	}

	/**
	 * set the user Id for the cLock client.
	 * 
	 * @param user
	 *            : User Id.
	 */
	public void setUser(String user) {
		cLockClient.user = user;
	}

	/**
	 * Shut down the heart beating thread.
	 */
	public void cleanup() {
		stpe.shutdown();
	}

	/**
	 * doOperation sends the message to the server and listens to server's
	 * reply. This communication between client-server is synchronous.
	 * 
	 * @param msg
	 *            : Message.
	 * @return : reply message from server.
	 * @throws LockException
	 *             : when the operation fails at client.
	 * @throws UnknownHostException
	 *             : when server cannot be reached.
	 * @throws IOException
	 *             : when message cannot be sent to server.
	 */
	public static Message doOperation(Message msg) throws LockException,
			UnknownHostException, IOException {

		if (serverURL == null || user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {

			Socket skt = new Socket(cLockClient.serverURL, 9091);
			DataOutputStream out = new DataOutputStream(skt.getOutputStream());
			DataInputStream in = new DataInputStream(skt.getInputStream());
			out.writeUTF(msg.toString());
			String str = in.readUTF();
			Message retmsg = cLockClient.mp.parse(str);
			return retmsg;

		}
	}
}
