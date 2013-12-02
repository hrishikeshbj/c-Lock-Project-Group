package clockserver;

import java.util.ArrayList;
import java.util.List;

public class LockRequest {

	String user;
	List<String> rList;
	
	public LockRequest(String user) {
		rList = new ArrayList<String>();
		this.user = user;
	}
	
	public void putRequest(String rId) {
		this.rList.add(rId);
	}
}
