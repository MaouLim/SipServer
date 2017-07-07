package bupt.database;

import java.util.List;

/*
 * Created by Maou Lim on 2017/7/4.
 */
public interface Query {

    List<Object> query(SoftConnection connection);
}
