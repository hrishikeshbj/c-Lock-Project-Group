package clockserver;
///
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestResourceManager {

	static ResourceTableManager rtm;
	
	@BeforeClass
	public static void setUp() throws SQLException {
		rtm = new ResourceTableManager();
		rtm.setupTable();
	}
	
	@Test
	public void testSetupTableEmptyDatabase() {
		try {
			
			rtm.setupTable();
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSetupTableWithTablePresentInDB() {
		try {
			
			rtm.setupTable();
			rtm.setupTable();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateResource() {
		try {
			
			
			Resource r = new Resource("R1Today", "R1", "Nachiket", "State1");
			rtm.createResource(r);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetResourcePositive() {
		try {
			
			
			Resource r = new Resource("R1Today", "R1", "Nachiket", "State1");
			rtm.createResource(r);
			
			r = new Resource("R1Today", "R1", "Nachiket", "State2");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State4");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State3");
			rtm.createResource(r);
			
			List<Resource> rlist = rtm.getResource("R1Today");
			
			for(Resource rs : rlist) {
				System.out.println(rs.toString());
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetResourceNegative() throws InterruptedException {
		try {
			
			Resource r = new Resource("R1Today", "R1", "Nachiket", "State1");
			rtm.createResource(r);
			
			r = new Resource("R1Today", "R1", "Nachiket", "State2");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State4");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State3");
			rtm.createResource(r);
			
			List<Resource> rlist = rtm.getResource("R1Today2");
			
			for(Resource rs : rlist) {
				System.out.println(rs.toString());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testQueryResourcePositive() {
		try {
			
			
			Resource r = new Resource("R1Today", "R1", "Nachiket", "State1");
			rtm.createResource(r);
			
			r = new Resource("R1Today", "R1", "Nachiket", "State2");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State4");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State3");
			rtm.createResource(r);
			
//			List<Resource> rlist = rtm.getResource("R1Today");
//			
//			for(Resource rs : rlist) {
//				System.out.println(rs.toString());
//			}
			
			System.out.println(rtm.queryResource("R2", "Nachiket"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testQueryResourceNegative() {
		try {
			
			
			Resource r = new Resource("R1Today", "R1", "Nachiket", "State1");
			rtm.createResource(r);
			
			r = new Resource("R1Today", "R1", "Nachiket", "State2");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State4");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State3");
			rtm.createResource(r);
			
//			List<Resource> rlist = rtm.getResource("R1Today");
//			
//			for(Resource rs : rlist) {
//				System.out.println(rs.toString());
//			}
			
			System.out.println(rtm.queryResource("R3", "Nachiket"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetAllResources() {
		try {
			
			
			Resource r = new Resource("R1Today", "R1", "Nachiket", "State1");
			rtm.createResource(r);
			
			r = new Resource("R1Today", "R1", "Nachiket", "State2");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State4");
			rtm.createResource(r);
			
			r = new Resource("R1Today1", "R2", "Nachiket", "State3");
			rtm.createResource(r);
			
			List<Resource> rlist = rtm.getAllResources();
			
			for(Resource rs : rlist) {
				System.out.println(rs.toString());
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
