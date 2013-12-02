package cLockClient;

import java.util.Map.Entry;

public class HeartBeat implements Runnable {

	@Override
	public void run() {

		if (cLockClient.resources.size() > 0) {
			if (cLockClient.serverURL != null && cLockClient.user != null) {

				for (Entry<String, Resource> entry : cLockClient.resources
						.entrySet()) {
					Resource rs = entry.getValue();
					rs.update();
				}
			}
		}
	}

}
