package clockserver;

public class Resource {
	private String rId;
	private String rName;
	private String user;
	private String rState;
	
	public Resource(String rId, String rName, String user, String rState) {
		this.rId = rId;
		this.rName = rName;
		this.user = user;
		this.rState = rState;
	}
	
	public String getrId() {
		return rId;
	}
	
	public String getrName() {
		return rName;
	}
	
	public String getuser() {
		return user;
	}
	
	public String getrState() {
		return rState;
	}
	
	public String toString() {
		String str = "<" + rId + ", " + rName + ", " + user + ", " + rState + ">";
		return str;
	}
}
