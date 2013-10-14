package cLockServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientListener extends Thread {

	final ExecutorService executor = Executors.newFixedThreadPool(10);
	private int client_port;
	Vector<ServerConnection> servers;
	private Queue messageQueue = new LinkedList<String>();
	ServerSocket socketForClient;
	HashMap<String, DLock> dLocks;

	ClientListener(int client_port, Vector<ServerConnection> servers, HashMap<String, DLock> dLocks)
			throws Exception {
		this.client_port = client_port;
		this.servers = servers;
		socketForClient = new ServerSocket(this.client_port);
		this.dLocks = dLocks;
	}

	public void run() {
		String message;
		while (true) {
			try {
				Socket sock;
				sock = socketForClient.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(sock.getInputStream()));
				message = inFromClient.readLine();

				Thread worker = new cLockClientWorker(message, sock, servers, dLocks);
				executor.execute(worker);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
