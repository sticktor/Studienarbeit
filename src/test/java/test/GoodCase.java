package test;

import org.apache.bcel.classfile.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

class GoodCase {
    ArrayList x = new ArrayList();
    void method(Tester map, Tester map2, Tester map3, Tester map4, Tester map5, Tester map6) {
        //TreeMap tm = new TreeMap();
        //GoodCase g = new GoodCase();
        //ArrayList t = new ArrayList();
        //t.add(new Object());
        //map.put("key", "value");
        //tm.clear();
        map.doSmth();
        map2.doSmth();
        map3.doSmth();
        map4.doSmth();
        map5.doSmth();
        map6.doSmth();
        meth();
        //map2.clear();
        //map.size();
    }

    void meth()
    {
        System.out.println("T");
    }
}
