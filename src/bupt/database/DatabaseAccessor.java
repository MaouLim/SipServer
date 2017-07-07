package bupt.database;

import java.util.List;

/*
 * Created by Maou on 2017/7/4.
 */
public class DatabaseAccessor {

    private ConnectionPool pool = null;

    public DatabaseAccessor() {
        pool = ConnectionPool.getInstance();
    }

    public List<Object> executeQuery(String actionName, Object... args) {
        return null;
    }

    private List<Object> execute(Query action) {
        SoftConnection connection = null;
        try {
            connection = pool.getConnection();
            return action.query(connection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            connection.release();
        }
        return null;
    }

    private int execute(Update action) {
        SoftConnection connection = null;
        try {
            connection = pool.getConnection();
            return action.update(connection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            connection.release();
        }
        return 0;
    }
}
