import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by vaibhavtulsyan on 22/07/16.
 */
public class ThreadTester {
    public static void main(String[] args) {
        test1(); // should report THREAD_INCONSISTENCY
        test2(); // should report THREAD_INCONSISTENCY
        test3(); // should not report RUN_INCONSISTENCY
        test4(); // should report RUN_INCONSISTENCY
        test5(); // should not report RUN_INCONSISTENCY (does not mean that it should report THREAD_INCONSISTENCY)
        test6(); // should not report RUN_INCONSISTENCY or THREAD_INCONSISTENCY
    }

    public static void test1() {
        MyExtendedThread thread = new MyExtendedThread();
        thread.run();
    }

    public static void test2() {
        new Thread(new Runnable()
        {
            public void run()
            {
                System.out.println("Tester");
            }
        }).run();
    }

    public static void test3() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        executorService.execute(new Runnable() {
            public void run() {
                System.out.println("Asynchronous task");
            }
        });
        executorService.shutdown();
    }

    public static void test4() {
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("hello");
            }
        };
        runnable.run();
    }

    public interface RandomClass {
        void func();
    }

    static class RunnableImplementer implements Runnable, RandomClass {
        public void run() {
            System.out.println("hi");
        }
        public void func() {
            System.out.println("This is func()");
        }
    }

    public static void test5() {
        RunnableImplementer r = new RunnableImplementer();
        r.run();
    }

    public static void test6() {
        RunnableImplementer r = new RunnableImplementer();
        Thread t = new Thread(r);
        t.start();
    }
}
