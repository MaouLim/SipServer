package bupt.database;

import java.util.List;

/*
 * Created by Maou on 2017/7/4.
 */
public class DatabaseAccessor {

    private ConnectionPool pool = null;

    private static DatabaseAccessor accessor = null;

    public static DatabaseAccessor getInstance() {
        if (null == accessor) {
            accessor = new DatabaseAccessor();
        }

        return accessor;
    }

    public List<Object> execute(Query action) {
        SoftConnection connection = null;
        try {
            connection = pool.getConnection();
            return action.query(connection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (null != connection) {
                connection.release();
            }
        }
        return null;
    }

    public int execute(Update action) {
        SoftConnection connection = null;
        try {
            connection = pool.getConnection();
            return action.update(connection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (null != connection) {
                connection.release();
            }
        }
        return 0;
    }

    private DatabaseAccessor() {
        pool = ConnectionPool.getInstance();
    }
}
