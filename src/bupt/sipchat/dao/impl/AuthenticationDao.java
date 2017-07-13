package bupt.sipchat.dao.impl;

import bupt.database.DatabaseAccessor;
import bupt.database.Query;
import bupt.database.SoftConnection;
import bupt.sipchat.dao.AuthenticationService;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Maou Lim on 2017/7/13.
 */
public class AuthenticationDao implements AuthenticationService {

    private DatabaseAccessor accessor = null;

    public AuthenticationDao() {
        accessor = DatabaseAccessor.getInstance();
    }

    @Override
    public String queryPassword(String userId) {
        Query queryObj = new QueryPassword(userId);
        List<Object> result = accessor.execute(queryObj);

        if (null == result || result.isEmpty()) {
            return "";
        }

        return (String) result.get(0);
    }

    private class QueryPassword implements Query {

        private String userId = null;

        public QueryPassword(String userId) {
            this.userId = userId;
        }

        @Override
        public List<Object> query(SoftConnection connection) {

            String sql = "SELECT password FROM userinfo WHERE useName = " + "\'" + userId + "\';";

            List<Object> results = new ArrayList<>(1);

            Statement statement = null;
            ResultSet resultSet = null;

            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);

                if (resultSet.next()){
                    results.add(resultSet.getString("password"));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            finally {
                try {
                    if (null != statement) {
                        statement.close();
                    }
                }
                catch (Exception ex) { }

                try {
                    if (null != resultSet) {
                        resultSet.close();
                    }
                }
                catch (Exception ex) { }

                connection.release();
            }

            return results;
        }
    }
}
