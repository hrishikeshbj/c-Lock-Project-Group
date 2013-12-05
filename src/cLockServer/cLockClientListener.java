package cLockServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Client Listener listens to client's requests. Converts the requests into an
 * instance of Message. This message contains all the information needed for
 * communication between the server and client and the servers. This message is
 * then passed to ServerManager. ServerManager takes appropriate action and
 * returns the message that is to be sent back. This message is then returned to
 * client.
 * 
 * @author Nachiket
 * 
 */
public class cLockClientListener {

	/**
	 * The server listens on listener socket for requests from user.
	 */
	private ServerSocket listener;

	/**
	 * Message Parser parses the message from user into an instance of Message
	 */
	private MessageParser mp = new MessageParser();

	/**
	 * Initialize the socket and start listening.
	 * 
	 * @throws IOException
	 *             : when the socket is already in use.
	 */
	public cLockClientListener() throws IOException {
		this.listener = new ServerSocket(9091);
	}

	/**
	 * Main method to initialize the server. THis method sets up the
	 * LockManager, Resource table manager. Then server starts to listen on
	 * ServerSocket for the requests from user.
	 * 
	 * @param args
	 * @throws IOException
	 *             : When a socket encounters a problem
	 * @throws SQLException
	 *             : when database operation encounters a problem in
	 *             ResourceTableManager.
	 * @throws InterruptedException
	 *             : When thread gets interrupted by system.
	 */
	public static void main(String[] args) throws IOException, SQLException,
			InterruptedException {

		// Set up the server manager with its configuration and the
		// ResourceTableManager.
		cLockServerManager.setup();

		// Start listening for client requests.
		cLockClientListener cl = new cLockClientListener();
		cl.listen();
	}

	/**
	 * Listen continuously to client requests. Once a request comes, convert the
	 * request to Message and pass the message to ServerManager for appropriate
	 * operation.
	 * 
	 * @throws InterruptedException
	 *             : When the thread gets interrupted by system.
	 */
	public void listen() throws InterruptedException {

		while (true) {
			try {
				// Accept the connection from user.
				Socket skt = listener.accept();
				
				// input stream to read data from user.
				DataInputStream in = new DataInputStream(skt.getInputStream());
				
				// output stream to write output to user.
				DataOutputStream out = new DataOutputStream(
						skt.getOutputStream());
				
				// read message String from user.
				String ln = in.readUTF();
				
				// convert the string into Message.
				Message msg = mp.parse(ln);
				
				//perform appropriate operation.
				msg = cLockServerManager.doOperation(msg);
				
				// Return the message to user.
				out.writeUTF(msg.toString());
				
				// close the connection.
				skt.close();
			} catch (IOException e) {
				if (!e.getMessage().contains("reset")) {
					e.printStackTrace();
				}
			}
		}
	}
}
