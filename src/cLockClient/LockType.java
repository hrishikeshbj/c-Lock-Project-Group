package cLockClient;

/**
 * Enumeration of lock types. The interoperability of these lockTypes is defined
 * by the severity of the locks.
 * 
 * @author Nachiket
 * 
 */
public enum LockType {
	NL, // Null lock
	CR, // Concurrent Read Lock
	CW, // Concurrent Write Lock
	PR, // Protected Read Lock
	PW, // Protected Write Lock
	XL; // Exclusive Lock

	/**
	 * Return the LockType based on the string parameter. The characters are
	 * exactly same to represent each lockType and the string that the lockType
	 * is represented with.
	 * 
	 * @param type
	 *            : String that contains the characters representing the
	 *            lockType.
	 * @return : the lockType represented by the characters in the input String.
	 *         If string doesn't match any lockType, null lock type is returned.
	 */
	public static LockType getLType(String type) {

		if (type.equalsIgnoreCase("NL")) {
			return NL;
		} else if (type.equalsIgnoreCase("CR")) {
			return CR;
		} else if (type.equalsIgnoreCase("CW")) {
			return CW;
		} else if (type.equalsIgnoreCase("PR")) {
			return PR;
		} else if (type.equalsIgnoreCase("PW")) {
			return PW;
		} else if (type.equalsIgnoreCase("XL")) {
			return XL;
		}
		return NL;
	}
}
