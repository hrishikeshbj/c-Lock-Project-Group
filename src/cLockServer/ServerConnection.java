package cLockServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection {
	
	private final String address;
	private final Socket clientSocket;
	private final BufferedReader socketReader;
	private final DataOutputStream socketWriter;
	
	ServerConnection(String address, int port) throws Exception {
		this.address = address;
		clientSocket = new Socket(address, port);
		socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		socketWriter = new DataOutputStream(clientSocket.getOutputStream());
	}
	
	public String getAddress() {
		return address;
	}
	
	public String readLine() throws IOException {
		return socketReader.readLine();
	}
	
	public void writeLine(String str) throws IOException {
		socketWriter.writeBytes(str + "\n");
	}
}
