package cLockClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class cLockClient {

	public static Map<String, Resource> resources = new HashMap<String, Resource>();
	final ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
	final HeartBeat hb = new HeartBeat();

	public static String serverURL;
	public static String user;

	public cLockClient() {
		serverURL = null;
		user = null;
		stpe.scheduleAtFixedRate(hb, 0, 60, TimeUnit.SECONDS);
	}

	public cLockClient(String serverURL, String user) {
		cLockClient.serverURL = serverURL;
		cLockClient.user = user;
		stpe.scheduleAtFixedRate(hb, 0, 60, TimeUnit.SECONDS);
	}

	public void setServerURl(String URL) {
		cLockClient.serverURL = URL;
	}

	public void setUser(String user) {
		cLockClient.user = user;
	}

	public Resource createResource(String rId, String rName,
			List<String> rStates) throws LockException {
		Resource rs;
		if (serverURL == null || user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			if (resources.get(rName) == null) {
				rs = new Resource(rId, rName, rStates);
				resources.put(rName, rs);
			} else {
				rs = resources.get(rName);
			}
		}
		return rs;
	}

	public Resource createResource(String rId, String rName)
			throws LockException {

		Resource rs;
		if (serverURL == null || user == null) {
			throw new LockException(LockErrorMessage.LERR_NOURL);
		} else {
			if (resources.get(rName) == null) {
				rs = new Resource(rId, rName);
				resources.put(rName, rs);
			} else {
				rs = resources.get(rName);
			}
		}
		return rs;
	}
}
