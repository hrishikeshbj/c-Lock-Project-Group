package cLockClient;

import java.util.List;

public class ClientHelper extends Thread{
	
	List<Resource> resources;
	
	ClientHelper(List<Resource> resources) {
		this.resources = resources;
	}
	
	public void run() {
		
	}
	
	private void heartBeat() {
		
	}
}
