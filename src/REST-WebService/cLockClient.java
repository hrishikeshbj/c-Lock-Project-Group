package cLockClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import cLockServer.Message;

public class cLockClient {

	//public static Map<String, Resource> resources = new HashMap<String, Resource>();
	//final ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
	//final HeartBeat hb = new HeartBeat();

	public static String serverURL;
	public static String user;

		public static void main (String [] args){
		serverURL = null;
		user = null;
	//	stpe.scheduleAtFixedRate(hb, 0, 60, TimeUnit.SECONDS);
		ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    WebResource service = client.resource(getBaseURI());
	    Message todo = new Message("3", "Blabla","3","4","5","6","7","8","9");
	    ClientResponse response = service.path("rest").path("message")
	            .accept(MediaType.APPLICATION_XML)
	            .post(ClientResponse.class, todo);
	    //service.path("rest").path("message").delete();
	    // Get the all todos, id 1 should be deleted
//	    System.out.println(service.path("rest").path("message")
//	        .accept(MediaType.APPLICATION_XML).post(String.class));
	    System.out.println(response.toString());
	    System.out.println("Form response " + response.getEntity(String.class));
	    System.out.println(service.path("rest").path("message")
	            .accept(MediaType.APPLICATION_XML).get(String.class));	    
		}	
	
//	public cLockClient(String serverURL, String user) {
//		cLockClient.serverURL = serverURL;
//		cLockClient.user = user;
//	//	stpe.scheduleAtFixedRate(hb, 0, 60, TimeUnit.SECONDS);
//	}

	public void setServerURl(String URL) {
		cLockClient.serverURL = URL;
	}

	public void setUser(String user) {
		cLockClient.user = user;
	}
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://localhost:8086/cLockServer").build();
	  }
}


//	public Resource createResource(String rId, String rName,
//			List<String> rStates) throws LockException {
//		Resource rs;
//		if (serverURL == null || user == null) {
//			throw new LockException(LockErrorMessage.LERR_NOURL);
//		} else {
//			if (resources.get(rName) == null) {
//				rs = new Resource(rId, rName, rStates);
//				resources.put(rName, rs);
//			} else {
//				rs = resources.get(rName);
//			}
//		}
//		return rs;
//	}
//
//	public Resource createResource(String rId, String rName)
//			throws LockException {
//
//		Resource rs;
//		if (serverURL == null || user == null) {
//			throw new LockException(LockErrorMessage.LERR_NOURL);
//		} else {
//			if (resources.get(rName) == null) {
//				rs = new Resource(rId, rName);
//				resources.put(rName, rs);
//			} else {
//				rs = resources.get(rName);
//			}
//		}
		//return rs;
		
		
		 
	
	
		
	
