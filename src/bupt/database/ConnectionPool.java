package bupt.database;

import bupt.util.Configuration;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

/*
 * Created by Maou on 2017/7/4.
 */
public class ConnectionPool {

    /* configurations */
    private static final Configuration
            DB_CONFIG = new Configuration("res/db-config.yml");

    static final String URL = (String) DB_CONFIG.get("url");
    static final String ACCOUNT = (String) DB_CONFIG.get("account");
    static final String PASSWORD = (String) DB_CONFIG.get("password");
    static final int INIT_CONNECTIONS = (Integer) DB_CONFIG.get("init_connections");
    static final int CORE_CONNECTIONS = (Integer) DB_CONFIG.get("core_connections");

    /* singleton */
    private static ConnectionPool pool = null;

    /* the components of a pool */
    private boolean available                        = false;
    private Queue<SoftConnection> idles              = null;
    private HashMap<String, SoftConnection> occupies = null;

    public static ConnectionPool getInstance() {
        if (null == pool) {
            pool = new ConnectionPool();
        }

        return pool;
    }

    public synchronized SoftConnection getConnection() throws Exception {
        if (!available) {
            throw new Exception("pool is not available.");
        }

        SoftConnection conn = idles.poll();
        if (null != conn) {
            occupies.put(conn.getHandle(), conn);
            return conn;
        }

        conn = createConnection();
        occupies.put(conn.getHandle(), conn);
        return conn;
    }

    public synchronized void setIdles(SoftConnection connection) {
        occupies.remove(connection.getHandle());

        if (CORE_CONNECTIONS <= idles.size()) {
            connection.close();
            return;
        }

        idles.add(connection);
    }

    public boolean isAvailable() {
        return available;
    }

    public synchronized void shutdown() {
        while (!idles.isEmpty()) {
            idles.poll().close();
        }

        Collection<SoftConnection> values =  occupies.values();
        for (SoftConnection each : values) {
            each.close();
        }
        occupies.clear();
    }

    private ConnectionPool() {
        assert initDriver();
        initPool();

        available = true;
    }

    private boolean initDriver() {
        try {
            Driver driver = (Driver) Class.forName(
                (String) DB_CONFIG.get("driver")
            ).newInstance();
            DriverManager.registerDriver(driver);
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    private void initPool() {
        idles = new LinkedList<>();
        occupies = new HashMap<>();

        for (int i = 0; i < INIT_CONNECTIONS; ++i) {
            try {
                idles.add(createConnection());
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private SoftConnection createConnection() throws SQLException {
        Connection conn =
                DriverManager.getConnection(URL, ACCOUNT, PASSWORD);
        return new SoftConnection(this, conn);
    }
}
