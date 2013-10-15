package cLockClient;

public class Resource {
	final String rname;
	final String rId;
	
	Resource(String rname, String rId) {
		this.rname = rname;
		this.rId = rId;
	}
	
	public String getrId() {
		return rId;
	}
}
