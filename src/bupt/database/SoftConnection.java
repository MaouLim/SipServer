package bupt.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Created by Maou on 2017/7/4.
 */
public class SoftConnection {

    private ConnectionPool pool       = null;
    private Connection     connection = null;

    public SoftConnection(ConnectionPool pool,
                          Connection connection) {
        assert (null != pool && null != connection);

        this.pool = pool;
        this.connection = connection;
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public void release() {
        pool.setIdles(this);
    }

    public void close() {
        try {
            if (connection.isClosed()) {
                return;
            }

            connection.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getHandle() {
        return this.toString();
    }
}
