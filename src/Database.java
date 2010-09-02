import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;


public class Database {
	public static String url;
	private static Logger log;

	static {
		url = "jdbc:mysql://localhost:3306/fractus";
		log = Logger.getLogger(Database.class.getName());
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e)
		{
			log.severe("Fatal error: could not load Database driver");
			Runtime.getRuntime().exit(-1);
		}
	}

	public static Connection getConnection() throws
	SQLException {
		return DriverManager.getConnection(url,"fractus", "");
	}    
}
