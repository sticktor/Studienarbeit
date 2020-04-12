package test;

class BadCase {
    public void method(char tester) {
        outer:
        for (int i = 2; i < 1000; i++) {
            for (int j = 2; j < i; j++) {
                if (i % j == 0)
                    return;
            }
            System.out.println (i);
        }
    }
}
