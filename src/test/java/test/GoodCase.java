package test;

import org.apache.bcel.classfile.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

class GoodCase {
    ArrayList x = new ArrayList();
    void method(Tester map) {
        //TreeMap tm = new TreeMap();
        //GoodCase g = new GoodCase();
        //ArrayList t = new ArrayList();
        //t.add(new Object());
        //map.put("key", "value");
        //tm.clear();
        map.doSmth();
        map.malleBois();
        map.x = "sda";
        String f = map.x;
        meth();
        //map2.clear();
        //map.size();
    }

    void meth()
    {
        System.out.println("T");
    }
}
