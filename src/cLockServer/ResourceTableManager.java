package cLockServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResourceTableManager {

	private Connection c;

	public ResourceTableManager() {
		try {
			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:cLock.db");
			c.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void setupTable() throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "DROP TABLE " + cLockConstants.resourceTableName + ";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// Do nothing.
		}

		sql = "CREATE TABLE " + cLockConstants.resourceTableName + " ("
				+ cLockConstants.RID + "     TEXT      NOT NULL,"
				+ cLockConstants.RNAME + "   TEXT      NOT NULL,"
				+ cLockConstants.USER + "    TEXT      NOT NULL,"
				+ cLockConstants.STATE + "   TEXT      NOT NULL" + ")";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}

	public void createResource(Resource r) throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "INSERT INTO " + cLockConstants.resourceTableName + " ("
				+ cLockConstants.RID + "," + cLockConstants.RNAME + ","
				+ cLockConstants.USER + "," + cLockConstants.STATE + ") "
				+ "VALUES ('" + r.getrId() + "', '" + r.getrName() + "', '"
				+ r.getuser() + "', '" + r.getrState() + "' );";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}

	public List<Resource> getResource(String rId) throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "SELECT * FROM " + cLockConstants.resourceTableName
				+ " WHERE " + cLockConstants.RID + "='" + rId + "';";

		ResultSet rs = stmt.executeQuery(sql);

		List<Resource> result = new ArrayList<Resource>();

		while (rs.next()) {
			String rid = rs.getString(cLockConstants.RID);
			String rname = rs.getString(cLockConstants.RNAME);
			String user = rs.getString(cLockConstants.USER);
			String rstate = rs.getString(cLockConstants.STATE);

			Resource r = new Resource(rid, rname, user, rstate);
			result.add(r);
		}
		rs.close();
		stmt.close();
		return result;
	}
	
	public List<Resource> getAllResources() throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "SELECT * FROM " + cLockConstants.resourceTableName + ";";

		ResultSet rs = stmt.executeQuery(sql);

		List<Resource> result = new ArrayList<Resource>();

		while (rs.next()) {
			String rid = rs.getString(cLockConstants.RID);
			String rname = rs.getString(cLockConstants.RNAME);
			String user = rs.getString(cLockConstants.USER);
			String rstate = rs.getString(cLockConstants.STATE);

			Resource r = new Resource(rid, rname, user, rstate);
			result.add(r);
		}
		
		rs.close();
		stmt.close();
		return result;
	}
	
	public String queryResource(String rName, String user) throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "SELECT " + cLockConstants.RID + " FROM " + cLockConstants.resourceTableName
				+ " WHERE " + cLockConstants.RNAME + "='" + rName + "' AND " + cLockConstants.USER + "='" + user + "';";

		ResultSet rs = stmt.executeQuery(sql);


		if (rs.next()) {
			String rid = rs.getString(cLockConstants.RID);
			rs.close();
			stmt.close();
			return rid;
		}
		return null;
	}
	
	public void deleteResource(String rId) throws SQLException {
	
		Statement stmt = c.createStatement();
		String sql = "DELETE FROM " + cLockConstants.resourceTableName
				+ " WHERE " + cLockConstants.RID + "='" + rId + "' ;";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}
}
