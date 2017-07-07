package bupt.database.test;

import bupt.database.Query;
import bupt.database.SoftConnection;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Maou on 2017/7/4.
 */
public class QueryStudentName implements Query {

    private String studentId;

    public QueryStudentName(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public List<Object> query(SoftConnection connection) {

        List<Object> nameList = new ArrayList<>();
        //....
        String name = "ddd";
        nameList.add((Object) name);
        return nameList;
    }
}
