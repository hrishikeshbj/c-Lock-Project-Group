package clockserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class for a Lock
 * 
 * @author Nachiket
 * 
 */

public class DLock {

	String rId;
	String state;
	List<SimpleLock> rLocks;

	public DLock(String rId, String state) {
		this.rId = rId;
		this.state = state;
		rLocks = new ArrayList<SimpleLock>();
	}

	public boolean createLock(String state, String user, LockType lt) {
//		System.out.println("createLock");
		if (!(this.state.equals(state))) {
			return false;
		} else if(!(getHighestLock().compareTo(LockType.XL) == 0)){
			if((lt == LockType.XL) && (getHighestLock().compareTo(LockType.NL) > 0)) {
				return false;
			}
			if (lt.compareTo(getHighestLock()) >= 0) {
				SimpleLock sLock = new SimpleLock(user, lt, 0);
				rLocks.add(sLock);
				return true;
			} 
		}
		return false;
	}
	
	public boolean canCreate(String state, String user, LockType lt) {
//		System.out.println("canCreate");
		if (!(this.state.equals(state))) {
			return false;
		} else if(!(getHighestLock().compareTo(LockType.XL) == 0)){
			if((lt == LockType.XL) && (getHighestLock().compareTo(LockType.NL) > 0)) {
				return false;
			}else if (lt.compareTo(getHighestLock()) >= 0) {
				return true;
			} 
		}
		return false;
	}
	private int searchLock(String user) {
		int i = -1;
		for(SimpleLock sl : rLocks) {
			i++;
			if(sl.user.equals(user)) {
				return i;
			}
		}
		return -1;
	}
	
	public void releaseLock(String state, String user, LockType lt) {

		int index = searchLock(user);
		if(index >= 0) {
			rLocks.remove(index);
		}
	}
	
	public void updateLock() {
		
		for(SimpleLock sl : rLocks) {
			sl.updateTime();
		}
	}
	
public boolean resetLock(String state, String user, LockType lt) {
		
		int index = searchLock(user);
		if(index >= 0) {
			SimpleLock sl = rLocks.get(index);
			sl.resetTime();
			rLocks.set(index, sl);
			return true;
		}
		return false;
	}

	public LockType getHighestLock() {
		if (rLocks.size() > 0) {
			SimpleLock hLock = rLocks.get(0);
			for (SimpleLock l : rLocks) {
				if (hLock.lt.compareTo(l.lt) < 0) {
					hLock = l;
				}
			}
			return hLock.lt;
		}
		return LockType.NL;
	}
}
