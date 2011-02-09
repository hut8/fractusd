package fractus.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * Database connector.
 * Pools connections like a boss.
 * Use only subclasses directly.
 * @author bowenl2
 *
 */
public class Database {
	public static String url;
	private final static Logger log;
	private static BoneCPConfig poolConfig;
	private static BoneCP connectionPool;

	static {
		log = Logger.getLogger(Database.class.getName());
		url = "jdbc:mysql://localhost:3306/fractus";

		log.info("Loading MySQL JDBC driver...");

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			log.info("MySQL JDBC driver loaded");
		} catch (Exception e) {
			log.fatal("Fatal error: could not load Database driver", e);
			Runtime.getRuntime().exit(-1);
		}

		// Connection pool
		poolConfig.setJdbcUrl(url);
		poolConfig.setUsername("fractus"); 
		poolConfig.setPassword("");
		poolConfig.setMinConnectionsPerPartition(5);
		poolConfig.setMaxConnectionsPerPartition(10);
		poolConfig.setPartitionCount(1);

		log.info("Establishing connection pool...");
		try {
			connectionPool = new BoneCP(poolConfig);
		} catch (SQLException e) {
			log.error("Could not create connection pool",e);
		}
		log.info("Connection pool established");
	}

	public static class Authenticator {
		public static boolean authenticate(UserCredentials credentials)
		throws SQLException {
			Connection conn = connectionPool.getConnection();
			try {
				PreparedStatement sth = conn.prepareStatement("CALL AuthenticateUser_prc(?,?)");
				sth.setString(1, credentials.getUsername());
				sth.setString(2, credentials.getPassword());
				sth.execute();
				ResultSet authRes = sth.getResultSet();

				if (authRes.first()) {
					log.debug("User authenticated passed: " + credentials.getUsername());
					return true;
				} else {
					log.debug("User authenticated failed: " + credentials.getUsername());
					return false;
				}
			} finally {
				conn.close();
			}
		}
	}
}
