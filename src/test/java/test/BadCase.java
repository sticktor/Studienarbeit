package test;


import java.util.ArrayList;
import java.util.Collection;

class BadCase {
    public static void method(Tester test) {
        Tester tester = new Tester();
        tester.doSmth();
        test.doSmth();
    }
}
