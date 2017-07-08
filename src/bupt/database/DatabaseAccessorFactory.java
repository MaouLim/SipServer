package bupt.database;

import bupt.util.Configuration;

import java.lang.reflect.InvocationTargetException;

/*
 * Created by Maou on 2017/7/4.
 */
public class DatabaseAccessorFactory {

    private static final Configuration
            DEFAULT_CONFIG = new Configuration("res/db-config.yml");

    private static DatabaseAccessorFactory factory = null;

    public static DatabaseAccessorFactory getInstance() {
        if (null == factory) {
            factory = new DatabaseAccessorFactory();
        }

        return factory;
    }

    public DatabaseAccessor
        createAccessor(Class<? extends DatabaseAccessor> type)
    throws NoSuchMethodException,
           IllegalAccessException,
           InvocationTargetException,
           InstantiationException {
        return type.getConstructor(Configuration.class).newInstance(DEFAULT_CONFIG);
    }
}
