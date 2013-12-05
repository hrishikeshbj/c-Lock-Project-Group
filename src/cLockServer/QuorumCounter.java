package cLockServer;

/**
 * QuorumCounter is just a counter to count positive or negative replies from
 * other servers for the Lock request by the user. Once Lock request is received
 * by server, it initiates quorum an asks other servers whether lock can be
 * granted or not. This counter counts the positive or negatice replies and when
 * asked decides whether the lock can be granted or not.
 * 
 * @author Nachiket
 * 
 */
public class QuorumCounter {

	private int positive;
	private int negative;

	/**
	 * Initiate quorum counter.
	 */
	public QuorumCounter() {
		this.positive = 0;
		this.negative = 0;
	}

	/**
	 * increase the counter value for positive replies.
	 */
	public void incPositive() {
		this.positive++;
	}

	/**
	 * increase the counter value for negative replies.
	 */
	public void incNegative() {
		this.negative++;
	}

	/**
	 * Get the decision of quorum. if the number of positive replies is greater
	 * than negative replies, true is returned. else if the number of negative
	 * replies is greater, negative is replied. This methos is called when the
	 * server listener gets ample replies.
	 * 
	 * @return true if positive , false if negative
	 */
	public boolean getDecision() {
		if (this.positive >= this.negative) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The server receives replies from other servers, The greate of number of
	 * positive replies or negative replies is returned.
	 * 
	 * @return Counter of greater of 2 positive or negative replies.
	 */
	public int getcounter() {
		if (this.positive > this.negative) {
			return this.positive;
		} else if (this.negative > this.positive) {
			return this.negative;
		} else {
			return this.positive;
		}
	}
}
