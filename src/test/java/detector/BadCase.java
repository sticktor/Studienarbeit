package detector;


class BadCase {
    public static void method(Tester test) {
        Tester tester = new Tester();
        tester.doSmth();
        test.doSmth();
    }
}
