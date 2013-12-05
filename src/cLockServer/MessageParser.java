package cLockServer;

import java.util.StringTokenizer;

/**
 * Message Parser that parses the message from string form into an instance of
 * Message. The fields in the string message are delimited by '|'.
 * 
 * @author Nachiket
 * 
 */
public class MessageParser {

	/**
	 * Parse the string.
	 * 
	 * @param line
	 *            : The string taht contains contents of a message
	 * @return : instance of class Message parsed fomr the input string.
	 */
	public Message parse(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, "|");
		String mId, nHops, rId, rName, rState, op, lType, desc, user;
		mId = tokenizer.nextToken();
		nHops = tokenizer.nextToken();
		rId = tokenizer.nextToken();
		rName = tokenizer.nextToken();
		rState = tokenizer.nextToken();
		op = tokenizer.nextToken();
		lType = tokenizer.nextToken();
		desc = tokenizer.nextToken();
		user = tokenizer.nextToken();
		Message msg = new Message(mId, nHops, rId, rName, rState, op, lType,
				desc, user);
		return msg;
	}

}
