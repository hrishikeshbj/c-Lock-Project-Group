package cLockServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import cLockServer.DLock.LockType;

public class cLockClientWorker extends Thread {

	String message;
	Vector<ServerConnection> servers;
	Socket sock;
	Map<String, DLock> dLocks;
	final StringTokenizer tokenizer;
	final DataOutputStream socketWriter;

	cLockClientWorker(String message, Socket sock,
			Vector<ServerConnection> servers, HashMap<String, DLock> dLocks)
			throws IOException {
		this.message = message;
		this.servers = servers;
		this.sock = sock;
		this.dLocks = dLocks;
		tokenizer = new StringTokenizer(message, "|");
		socketWriter = new DataOutputStream(sock.getOutputStream());
	}

	public void run() {
		try {
			String kind = tokenizer.nextToken();
			if (kind.equalsIgnoreCase("resource")) {
				manageResource();
			} else if (kind.equalsIgnoreCase("lock")) {
				manageLock();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void manageResource() throws Exception {
		if (tokenizer.hasMoreTokens()) {
			String op = tokenizer.nextToken();
			Connection c = null;
			Statement stmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:test.db");
				c.setAutoCommit(false);
			} catch (Exception e) {
				throw new RuntimeException("Cannot create Database connection");
			}

			if (op.equalsIgnoreCase("create")) {
				String rname = tokenizer.nextToken();
				String usr = tokenizer.nextToken();
				String state = tokenizer.nextToken();
				String rid = rname + ":" + usr + ":"
						+ UUID.randomUUID().toString();
				try {
					stmt = c.createStatement();
					String sql = "INSERT INTO RESOURCE (RID,RNAME,USER,STATE) "
							+ "VALUES ('" + rid + "', '" + rname + "', ' "
							+ usr + "', '" + state + "');";
					stmt.executeUpdate(sql);
					stmt.close();
					c.commit();
					c.close();
					sock.close();
				} catch (SQLException e) {
					throw new RuntimeException(
							"Unable to create resource record");
				}

			} else if (op.equalsIgnoreCase("get")) {
				String rname = tokenizer.nextToken();
				String usr = tokenizer.nextToken();
				try {
					stmt = c.createStatement();
					String sql = "SELECT RID FROM RESOURCE  " + "WHERE(RNAME='"
							+ rname + "' AND USER='" + usr + "');";
					ResultSet rs = stmt.executeQuery(sql);
					rs.next();
					String rid = rs.getString("RID");
					stmt.close();
					c.close();
					this.socketWriter.writeBytes(rid + "\n");
					sock.close();
				} catch (SQLException e) {
					throw new RuntimeException(
							"Unable to retrieve resource record");
				}
			} else if (op.equalsIgnoreCase("delete")) {
				String rid = tokenizer.nextToken();
				try {
					stmt = c.createStatement();
					String sql = "DELETE from RESOURCE where RID='" + rid
							+ "';";
					stmt.executeUpdate(sql);
					stmt.close();
					c.commit();
					c.close();
					sock.close();
				} catch (SQLException e) {
					throw new RuntimeException(
							"Unable to create resource record");
				}
			}
		} else {
			throw new RuntimeException("Invalid Message");
		}
	}

	private void manageLock() throws Exception {
		String rid = tokenizer.nextToken();
		String rstate = tokenizer.nextToken();
		String ltype = tokenizer.nextToken();
		DLock lock = dLocks.get(rid);

		if (lock.state.equals(rstate)) {

			switch (lock.getHighestLock()) {
			case NL: {
				this.socketWriter.writeBytes("grant");
				break;
			}
			case CR: {
				if (ltype.equals(LockType.XL.toString())) {
					this.socketWriter.writeBytes("reject");
				} else {
					runQuorum();
				}
				break;
			}
			case CW: {
				if (ltype.equals(LockType.XL.toString())
						|| ltype.equals(LockType.PR.toString())
						|| ltype.equals(LockType.PW.toString())) {
					this.socketWriter.writeBytes("reject");
				} else {
					runQuorum();
				}
				break;
			}
			case PR: {
				if (ltype.equals(LockType.XL.toString())
						|| ltype.equals(LockType.CW.toString())
						|| ltype.equals(LockType.PW.toString())) {
					this.socketWriter.writeBytes("REJECT");
				} else {
					runQuorum();
				}
				break;
			}
			case PW: {
				if (ltype.equals(LockType.XL.toString())
						|| ltype.equals(LockType.CW.toString())
						|| ltype.equals(LockType.PW.toString())
						|| ltype.equals(LockType.PR.toString())) {
					this.socketWriter.writeBytes("REJECT");
				} else {
					runQuorum();
				}
				break;
			}
			case XL: {
				this.socketWriter.writeBytes("reject");
			}
			
			}// End switch
		} else {
			this.socketWriter.writeBytes("REJECT");
		}
	}

	public void runQuorum() {
		
	}
}
