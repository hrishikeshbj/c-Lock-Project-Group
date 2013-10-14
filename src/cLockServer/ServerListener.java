package cLockServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

public class ServerListener extends Thread {

	final ExecutorService executor = Executors.newFixedThreadPool(10);
	private int server_port;
	private Queue messageQueue = new LinkedList<String>();
	Vector<ServerConnection> servers;
	ServerSocket serverSocket;
	HashMap<String, DLock> dLocks;

	ServerListener(int server_port, Vector<ServerConnection> servers, HashMap<String, DLock> dLocks)
			throws Exception {
		this.server_port = server_port;
		this.servers = servers;
		serverSocket = new ServerSocket(server_port);
		this.dLocks = dLocks;
	}

	public void run() {

		String message;
		while (true) {
			try {
				Socket sock;
				sock = serverSocket.accept();
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
