package bupt.database.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/*
 * Created by Maou on 2017/7/4.
 */

abstract class Base {

    Integer n;

    public Base(Integer n) {
        this.n = n;
    }

    public abstract void handleSth();
}

class Child extends Base {

    public Child(Integer n) {
        super(n);
    }

    @Override
    public void handleSth() {
        System.out.println("do something...");
    }
}
public class A {


    Base
        create(Class<? extends Base> type)
    throws NoSuchMethodException,
           IllegalAccessException,
           InvocationTargetException,
           InstantiationException {
        Constructor[] ctors = type.getConstructors();
        return type.getConstructor(Integer.class).newInstance(Integer.valueOf(10));
    }

    public static void main(String[] args) {
        A a = new A();
        try {
            Base b = a.create(Child.class);

            b.handleSth();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }
}


