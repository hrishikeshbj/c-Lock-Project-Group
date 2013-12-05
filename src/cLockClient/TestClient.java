package cLockClient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestClient {
	cLockClient cl = new cLockClient();

	public TestClient() {
		cl.setServerURl("localhost");
		cl.setUser("Nachiket");
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException, LockException, InterruptedException {

		TestClient tc = new TestClient();
		tc.test();
		return;
	}

	public void test() throws UnknownHostException, LockException, IOException,
			InterruptedException {

		//List<Resource> res = createthousand();
		// createtenthousand();

		// queryOne();

		// queryOneDiffId();

		lockOne();
		// lockOneSameState();
		//lockSame();
		//lockThousand(res);

		//testDeadLock();

		// queryanddelete();
		// queryOne();

		// testLeader();

		// cl.cleanup();
	}

	public void lockOne() throws UnknownHostException, LockException,
			IOException, InterruptedException {
		Resource rs = new Resource("RNAME");
		rs.setRId("RNAMEID");
		rs.addState("STATE");
		if (rs.lock(LockType.CW, "STATE")) {
			System.out.println("Lock Success.");
		} else {
			System.out.println("Lock Failed.");
		}
		System.out.println("Wait for 2 minutes");
		Thread.sleep(30000);
		System.out.println("Releasing the lock");
		rs.release();
	}

	public void lockOneSameState() throws UnknownHostException, LockException,
			IOException {
		List<String> sList = new ArrayList<String>();
		sList.add("STATE");
		Resource rs = new Resource("RNAMEID", "RNAME", sList);
		if (rs.lock(LockType.CR, "STATE")) {
			System.out.println("Lock Success.");
		} else {
			System.out.println("Lock Failed.");
		}
	}

	public void testDeadLock() throws UnknownHostException, LockException,
			IOException, InterruptedException {

		Resource rs = new Resource("A");
		rs.generateRId();
		rs.addState("STATE");
		if (rs.lock(LockType.XL, "STATE")) {
			System.out.println("Resource 'A' locked by user 'Nachiket'.");
		}

		Thread.sleep(10000);

		Resource rs1 = new Resource("B");
		rs1.addState("STATE", false);
		rs1.retrieveRId("Nachiket1");
		if (rs1.lock(LockType.CR, "STATE")) {
			System.out.println("Resource 'B' locked by user 'Nachiket'.");
		} else {
			System.out.println("Resource 'B' NOT locked by user 'Nachiket'.");
		}

		for (int i = 0; i < 10; i++) {
			Thread.sleep(10000);
			LockError le = rs.checkLock();
			System.out.println("A : " + le);
		}
	}

	public void lockSame() throws UnknownHostException, LockException,
			IOException {

		System.out
				.println("Trying to lock same resource twice. Expected an Exception.");
		Resource rs = new Resource("RNAME");
		rs.generateRId();
		rs.addState("STATE");
		if (rs.lock(LockType.CR, "STATE")) {
			System.out.println("Lock Success.");
		} else {
			System.out.println("Lock Failed.");
		}

		try {
			if (rs.lock(LockType.CR, "STATE")) {
				System.out.println("Lock Success.");
			} else {
				System.out.println("Lock Failed.");
			}
		} catch (LockException e) {
			e.printStackTrace();
		}
	}

	public void testLeader() throws UnknownHostException, LockException,
			IOException, InterruptedException {
		LeaderElectionCoordinator lec = new LeaderElectionCoordinator("TestApp");
		while (!lec.amITheLeader()) {
			System.out.println("Not the leader, wait for it");
			Thread.sleep(10000);
		}
		System.out.println("I am the Leader");
		Thread.sleep(30000);
		System.out.println("Abjugating leadership.");
		lec.relieve();
	}

	public void runone() throws UnknownHostException, LockException,
			IOException {
		Resource rs = new Resource("RNAME");
		rs.generateRId();
		rs.addState("STATE");

		Resource rs1 = new Resource("RNAME");
		System.out.println(rs1.retrieveRId());
	}

	public void queryOne() throws UnknownHostException, LockException,
			IOException {
		int i = new Random().nextInt(1000);
		System.out.println("RNAME" + i);
		Resource rs1 = new Resource("RNAME" + i);
		System.out.println(rs1.retrieveRId());
	}

	public void queryOneDiffId() throws UnknownHostException, LockException,
			IOException {
		int i = new Random().nextInt(1000);
		System.out.println("RNAME" + i);
		Resource rs1 = new Resource("RNAME" + i);
		System.out.println(rs1.retrieveRId("Nachiket1"));
	}

	public List<Resource> createthousand() throws UnknownHostException,
			LockException, IOException {
		long ct = System.currentTimeMillis();
		List<Resource> res = new ArrayList<Resource>();
		for (int i = 0; i < 1000; i++) {
			Resource rs = new Resource("RNAME" + i);
			rs.generateRId();
			rs.addState("STATE");
			res.add(rs);
		}
		long lt = System.currentTimeMillis();
		System.out.println("Time taken to create ten thousand resources in miliseconds = " + (lt - ct));
		return res;
	}

	public void lockThousand(List<Resource> res) throws UnknownHostException,
			LockException, IOException {
		long ct = System.currentTimeMillis();
		for (Resource rs : res) {
			rs.lock(LockType.XL, "STATE");
		}
		long lt = System.currentTimeMillis();
		System.out.println("Time taken to lock thousand in miliseconds = " + (lt - ct));
	}

	public void createtenthousand() throws UnknownHostException, LockException,
			IOException {
		long ct = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			Resource rs = new Resource("RNAME" + i);
			rs.generateRId();
			for (int j = 0; j < 10; j++) {
				rs.addState("STATE");
			}
		}
		long lt = System.currentTimeMillis();
		System.out.println("Time taken to create ten thousand in miliseconds = " + (lt - ct));
	}

	public void query() throws UnknownHostException, LockException, IOException {
		// List<String> ids = new ArrayList<String>();
		long ct = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			Resource rs = new Resource("RNAME" + i);
			String str = rs.retrieveRId();
			System.out.println(str);
			// ids.add(str);
		}
		long lt = System.currentTimeMillis();
		System.out.println("Time taken to retrieve thousand in miliseconds = " + (lt - ct));
	}

	public void queryanddelete() throws UnknownHostException, LockException,
			IOException {
		List<String> ids = new ArrayList<String>();
		long ct = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			Resource rs = new Resource("RNAME" + i);
			String str = rs.retrieveRId();
			//System.out.println(str);
			ids.add(str);
		}
		long lt = System.currentTimeMillis();
		System.out.println("Time taken to retrieve thousand in miliseconds = " + (lt - ct));

		ct = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			Resource rs = new Resource("RNAME" + i);
			rs.setRId(ids.get(i));
			rs.delete();
		}
		lt = System.currentTimeMillis();
		System.out.println("Time taken to delete thousand in miliseconds = " + (lt - ct));
	}
}
