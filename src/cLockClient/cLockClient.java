package cLockClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class cLockClient {
	
	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	String address;
	String user;
	String auth;
	List<Resource> rList;
	ClientHelper helper;
	
	cLockClient(String address, String user, String auth) {
		helper =  new ClientHelper(rList);
		this.address = address;
		this.user = user;
		this.auth = auth;
	}
	
	public static enum LockType {
		NL, 		// Null lock 
		CR,			// Concurrent Read Lock 
		CW,			// Concurrent Write Lock 
		PR,			// Protected Read Lock 
		PW,			// Protected Write Lock 
		XL; 		// Exclusive Lock
		
	}
	
	public void createResource(Resource rs) {
		
	}
	
	public void getrId(Resource rs) {
		
	}
	
	public void deleteResource(Resource rs) {
		
	}
	
	public boolean lock(Resource rs, LockType type) {
		return true;
	}
	

}
