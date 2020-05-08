package test;


import java.util.ArrayList;
import java.util.Collection;

class BadCase {
    public static boolean method(ArrayList<Integer> test) {
        boolean x = test.contains(2);
        return x;
    }
}
