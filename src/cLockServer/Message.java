package cLockServer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
	private String mId;
	private String nHops;
	private String rId;
	private String rName;
	private String rState;
	private String op;
	private String lType;
	private String desc;
	private String user;

	public Message() {
		
	}
	public Message(String mId, String nHops, String rId, String rName,
			String rState, String op, String lType, String desc, String user) {
		this.mId = mId;
		this.nHops = nHops;
		this.rId = rId;
		this.rName = rName;
		this.rState = rState;
		this.op = op;
		this.lType = lType;
		this.desc = desc;
		this.user = user;
	}

	public void setMId(String mId) {
		this.mId = mId;
	}

	public String getMId() {
		return this.mId;
	}

	public void setNHops(String nHops) {
		this.nHops = nHops;
	}

	public String getnHops() {
		return this.nHops;
	}

	public void setRId(String rId) {
		this.rId = rId;
	}

	public String getRId() {
		return this.rId;
	}

	public void setRName(String rName) {
		this.rName = rName;
	}

	public String getRName() {
		return this.rName;
	}

	public void setRState(String rState) {
		this.rState = rState;
	}

	public String getRState() {
		return this.rState;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getOp() {
		return this.op;
	}

	public void setLType(String lType) {
		this.lType = lType;
	}

	public String getLType() {
		return this.lType;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return this.user;
	}
}
