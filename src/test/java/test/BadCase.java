package test;

class BadCase {
    public Object method(Tester test) {
        test.doSmth();

        return new Object();
    }
}
