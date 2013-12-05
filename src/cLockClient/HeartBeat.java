package cLockClient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map.Entry;

/**
 * Thread to send heart beat to the server. For all the resource locks created
 * by local user, this thread sends update message to the server to maintain the
 * lock. This message is scheduled to work periodically using Scheduled Thread
 * Pool.
 * 
 * 
 * 
 */
public class HeartBeat implements Runnable {

	@Override
	public void run() {
		// System.out.println("HeartBeat");
		// Run only if there are resources created by this client.
		if (cLockClient.resources.size() > 0) {

			// ignore if serverURL or user Ids are not set.
			if (cLockClient.serverURL != null && cLockClient.user != null) {

				// for each resource created
				for (Entry<String, Resource> entry : cLockClient.resources
						.entrySet()) {
					Resource rs = entry.getValue();
					try {
						// if lock is created for the resource
						if (rs.getLockType() != null) {
							// System.out.println(rs.getRName() + " | "
							// + rs.getLockType());
							// Update the resource lock.
							rs.update();
						}
					} catch (UnknownHostException e) {
						// ignore
					} catch (LockException e) {
						// ignore
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

}
