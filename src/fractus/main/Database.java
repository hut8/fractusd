package fractus.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class Database {

    public static String url;
    private final static Logger log;

    static {
        log = Logger.getLogger(Database.class.getName());
        url = "jdbc:mysql://localhost:3306/fractus";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            log.fatal("Fatal error: could not load Database driver", e);
            Runtime.getRuntime().exit(-1);
        }
    }

    public static Connection getConnection() throws
            SQLException {
        return DriverManager.getConnection(url, "fractus", "");
    }

    public static boolean authenticate(UserCredentials credentials)
            throws SQLException {
        Connection db = null;
            db = Database.getConnection();
            PreparedStatement sth = db.prepareStatement("CALL AuthenticateUser_prc(?,?)");
            sth.setString(1, credentials.getUsername());
            sth.setString(2, credentials.getPassword());
            sth.execute();
            ResultSet authRes = sth.getResultSet();

            if (authRes.first()) {
                db.close();
                log.debug("User authenticated passed: " + credentials.getUsername());
                return true;
            } else {
                log.debug("User authenticated failed: " + credentials.getUsername());
                return false;
            }
    }
}
