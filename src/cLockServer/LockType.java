package cLockServer;

public enum LockType {
	NL, 		// Null lock 
	CR,			// Concurrent Read Lock 
	CW,			// Concurrent Write Lock 
	PR,			// Protected Read Lock 
	PW,			// Protected Write Lock 
	XL; 		// Exclusive Lock
	
	public static LockType getLType(String type) {
		
		if(type.equalsIgnoreCase("NL")) {
			return NL;
		} else if(type.equalsIgnoreCase("CR")) {
			return CR;
		} else if(type.equalsIgnoreCase("CW")) {
			return CW;
		} else if(type.equalsIgnoreCase("PR")) {
			return PR;
		} else if(type.equalsIgnoreCase("PW")) {
			return PW;
		} else if(type.equalsIgnoreCase("XL")) {
			return XL;
		}
		return NL;
	}
}
