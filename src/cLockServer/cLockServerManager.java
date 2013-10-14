/*
 * ServerManager for c-Lock the Distributed Lock Manager.
 */

package cLockServer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.sql.*;

public class cLockServerManager {
	
	static HashMap<String, DLock> dLocks;
	static Vector servers;
	final int client_port = 33333;
	final int server_port = 33334;
	
	cLockServerManager() throws IOException {
		servers = new Vector<ServerConnection>();
		dLocks = new HashMap<String, DLock>();
	}

	public static void main() throws Exception {
		
		cLockServerManager cLockServer = new cLockServerManager();
		
		cLockServer.setupServer();
		
		cLockServer.listen();
		
	}
	
	/**
	 * Sets up connections with other servers.
	 * @throws Exception
	 */
	private void setupServer() throws Exception {
		
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Opened database successfully");

		      stmt = c.createStatement();
		      String sql = "CREATE TABLE RESOURCE " +
		                   "(RID 			TEXT    NOT NULL," +
		                   " RAME           TEXT    NOT NULL, " + 
		                   " USER           TEXT    NOT NULL, " + 
		                   " STATE          TEXT)"; 
		      stmt.executeUpdate(sql);
		      stmt.close();
		      c.close();
		} catch (Exception e) {
			// ignore.
		}
		
		
		BufferedReader read = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/config.txt"));
		String line = null;
		while ((line = read.readLine()) != null) {
			ServerConnection con = new ServerConnection(line, server_port);
			servers.add(con);
		}
		read.close();
		
		// Other servers are found in the config file.
		if(servers.size() > 0) {
			ServerConnection server = (ServerConnection) servers.get(0);
			server.writeLine("hello");
			String adds = server.readLine();
			StringTokenizer tokenizer = new StringTokenizer(adds, "|");
			while (tokenizer.hasMoreTokens()) {
				ServerConnection con = new ServerConnection(line, server_port);
				servers.add(con);
			}
		} else {
			//throw new RuntimeException("Unable to establish connection with any other server");
		}
		
		// Start the operation threads here
	}
	
	private void listen() throws Exception {
		ServerListener serverListener = new ServerListener(server_port, servers, dLocks);
		ClientListener clientListener = new ClientListener(client_port, servers, dLocks);
		serverListener.start();
		clientListener.start();
	}
}

//// END////
