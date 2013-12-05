package cLockServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Table manager for the cLock Engine. This class performs all the operations
 * associated with maintaining the database of resources on the server.
 * 
 * @author Nachiket
 * 
 */
public class ResourceTableManager {

	/**
	 * database connection
	 */
	private Connection c;

	/**
	 * Establish the database connection.
	 */
	public ResourceTableManager() {
		try {
			Class.forName("org.sqlite.JDBC");

			// we use sqllite database.
			c = DriverManager.getConnection("jdbc:sqlite:cLock.db");
			c.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set up the resources table in the database.
	 * 
	 * @throws SQLException
	 *             : if table creation fails.
	 */
	public void setupTable() throws SQLException {

		// at each new run of the server manager, drop the table of old
		// resources.
		Statement stmt = c.createStatement();
		String sql = "DROP TABLE " + cLockConstants.resourceTableName + ";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// Do nothing.
		}

		// the ncreate new table with fresh resources.
		sql = "CREATE TABLE " + cLockConstants.resourceTableName + " ("
				+ cLockConstants.RID + "     TEXT      NOT NULL,"
				+ cLockConstants.RNAME + "   TEXT      NOT NULL,"
				+ cLockConstants.USER + "    TEXT      NOT NULL,"
				+ cLockConstants.STATE + "   TEXT      NOT NULL" + ")";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}

	/**
	 * create a record of resource in the resource table.
	 * 
	 * @param r
	 *            : a resource to be created in teh table.
	 * @throws SQLException
	 *             : when creating the resource in table fails.
	 */
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

	/**
	 * query the resource from the table to get list of states associated with
	 * it.
	 * 
	 * @param rId
	 *            : resource id to search in the DB.
	 * @return : list or resources queried from database.
	 * @throws SQLException
	 *             : if query of table fails.
	 */
	public List<Resource> getResource(String rId) throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "SELECT * FROM " + cLockConstants.resourceTableName
				+ " WHERE " + cLockConstants.RID + "='" + rId + "';";

		ResultSet rs = stmt.executeQuery(sql);

		List<Resource> result = new ArrayList<Resource>();

		// extract the resource from the Resultset.
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

	/**
	 * Get the list of all resources from the table.
	 * 
	 * @return : list of all resources in the database.
	 * @throws SQLException
	 *             : if query fails .
	 */
	public List<Resource> getAllResources() throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "SELECT * FROM " + cLockConstants.resourceTableName + ";";

		ResultSet rs = stmt.executeQuery(sql);

		List<Resource> result = new ArrayList<Resource>();

		// extract the resource from result set into a list.
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

	/**
	 * Query a resource based on its name and user to get its resource Id.
	 * 
	 * @param rName
	 *            : name of the resource
	 * @param user
	 *            : user who created the resource
	 * @return : resource Id.
	 * @throws SQLException
	 *             : if query on database fails.
	 */
	public String queryResource(String rName, String user) throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "SELECT " + cLockConstants.RID + " FROM "
				+ cLockConstants.resourceTableName + " WHERE "
				+ cLockConstants.RNAME + "='" + rName + "' AND "
				+ cLockConstants.USER + "='" + user + "';";

		ResultSet rs = stmt.executeQuery(sql);

		if (rs.next()) {
			String rid = rs.getString(cLockConstants.RID);
			rs.close();
			stmt.close();
			return rid;
		}
		return null;
	}

	/**
	 * Delete teh resource from the database. All the states associated with the
	 * resource Id are also deleted.
	 * 
	 * @param rId
	 *            : uniue id of the resource to be deleted.
	 * @throws SQLException : if the delete query on DB fails.
	 */
	public void deleteResource(String rId) throws SQLException {

		Statement stmt = c.createStatement();
		String sql = "DELETE FROM " + cLockConstants.resourceTableName
				+ " WHERE " + cLockConstants.RID + "='" + rId + "' ;";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}
}
