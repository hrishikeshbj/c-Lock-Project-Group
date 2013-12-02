package clockserver;

public class SimpleLock {
		public String user;
		public LockType lt;
		public int lockTime;
		public boolean deadLock;
		
		public SimpleLock(String usr, LockType lt, int lockTime) {
			this.user = usr;
			this.lt = lt;
			this.lockTime = lockTime;
			this.deadLock = false;
		}
		
		public void resetTime() {
			this.lockTime = 0;
		}
		
		public void updateTime() {
			this.lockTime ++;
		}
		
		public void setDeadLock() {
			this.deadLock = true;
		}
}
