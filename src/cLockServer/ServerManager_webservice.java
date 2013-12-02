package cLockServer;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public enum ServerManager {
	instance;

//	//public final static ResourceTableManager rtm = new ResourceTableManager();
//	public final static DLockManager dlm = new DLockManager(rtm);
//	public static Map<String, LockRequest> requests = new HashMap<String, LockRequest>();
	public static final List<String> servers = new ArrayList<String>();
	final ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);

	ServerManager() {
	}

	public static Message doOperation(Message msg) {

//		Message retmsg = null;
//		try {
//
//			if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_CREATE)) {
//				Resource rs = new Resource(msg.getRId(), msg.getRName(),
//						msg.getUser(), msg.getRState());
//				rtm.createResource(rs);
//				retmsg = msg;
//				// TODO forward it
//			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_GET)) {
//
//				String rId = rtm.queryResource(msg.getRName(), msg.getUser());
//				msg.setRId(rId);
//				retmsg = msg;
//			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_DELETE)) {
//
//				rtm.deleteResource(msg.getRId());
//				// TODO forward it.
//			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_LOCK)) {
//
//				cLockError le = dlm.createLock(msg.getRId(), msg.getRState(),
//						msg.getUser(), LockType.getLType(msg.getLType()));
//				if (le.equals(cLockError.NE)) {
//					// TODO Forward Quorum!!!
//					msg.setOp(cLockConstants.OP_SUCCESS);
//					retmsg = msg;
//				} else {
//					// add the request to the list of failed requests.
//
//					LockRequest lr = requests.get(msg.getUser());
//					if (lr == null) {
//						lr = new LockRequest(msg.getUser());
//					} else {
//						lr.rList.add(msg.getRId());
//					}
//					requests.put(msg.getUser(), lr);
//					msg.setOp(cLockConstants.OP_FAIL);
//					retmsg = msg;
//				}
//			} else if (msg.getOp().equalsIgnoreCase(cLockConstants.OP_RELEASE)) {
//
//				dlm.releaseLock(msg.getRId(), msg.getRState(), msg.getUser(),
//						LockType.getLType(msg.getLType()));
//				// TODO forward
//			} else if (msg.getOp()
//					.equalsIgnoreCase(cLockConstants.OP_CANCREATE)) {
//				boolean bl = dlm.canCreate(msg.getRId(), msg.getRState(), msg.getUser(), LockType.getLType(msg.getLType()));
//				if(bl) {
//					msg.setOp(cLockConstants.OP_SUCCESS);
//				} else {
//					msg.setOp(cLockConstants.OP_FAIL);
//				}
//				retmsg = msg;
//			}
//		} catch (SQLException e) {
//			// TODO Log it.
//		}
//
		System.out.println(msg.toString());
		return msg;
		
	}

//	public void addRequest(String rId, String state, String user, LockType lt)
//			throws SQLException {
//
//		// search for request in list of current failed requests.
//		LockRequest lr = requests.get(user);
//		if (lr == null) {
//
//			// create the request
//			lr = new LockRequest(user);
//			lr.putRequest(rId);
//			requests.put(user, lr);
//		} else {
//			lr.putRequest(rId);
//		}
//	}

}

