import balancer.Balancer;
import continuation.Priority;
import continuation.StatedContinuation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NewMain {

    public static void main(String[] args) {

        Balancer balancer = new Balancer(4, false, 0.15D);

        final var t1 = new Runnable() {
            @Override
            public void run() {
//                System.out.println("t1 LOW");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                long count = 0;
                while (count < 100_000_000L) {
                    if (count++ % 10_000_000 == 0) {
//                        System.out.println("t1 LOW " + count);
                    }
                }
            }
        };

        final var t2 = new Runnable() {
            @Override
            public void run() {
                long count = 0;
                while (count < 100_000_000L) {
                    if (count++ % 10_000_000 == 0) {
//                        System.out.println("t2 MEDIUM " + count);
                    }
                }
//                System.out.println("t2 MEDIUM");
//                while (true){}
            }
        };

        final var t3 = new Runnable() {
            @Override
            public void run() {
                long count = 0;
                while (count < 100_000_000L) {
                    if (count++ % 10_000_000 == 0) {
//                        System.out.println("t3 HIGH " + count);
                    }
                }
//                System.out.println("t3 HIGH");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        };

        final var t4 = new Runnable() {
            @Override
            public void run() {
                long count = 0;
                while (count < 100_000_000L) {
                    if (count++ % 10_000_000 == 0) {
//                        System.out.println("t4 HIGH " + count);
                    }
                }
//                System.out.println("t4 HIGH");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        };

        final var t5 = new Runnable() {
            @Override
            public void run() {
                long count = 0;
                while (count < 100_000_000L) {
                    if (count++ % 10_000_000 == 0) {
//                        System.out.println("t5 LOW " + count);
                    }
                }
//                System.out.println("t5 LOW");
            }
        };

        final var t6 = new Runnable() {
            @Override
            public void run() {
                long count = 0;
                while (count < 100_000_000L) {
                    if (count++ % 10_000_000 == 0) {
//                        System.out.println("t6 MEDIUM " + count);
                    }
                }
//                System.out.println("t6 MEDIUM");
            }
        };

//        balancer.addTask(t1, Priority.LOW);
//        balancer.addTask(t2, Priority.MEDIUM);
//        balancer.addTask(t3, Priority.HIGH);
//        balancer.addTask(t4, Priority.HIGH);
//        balancer.addTask(t5, Priority.LOW);
//        balancer.addTask(t6, Priority.MEDIUM);

        new Thread(() -> {
            for (int j = 0; j < 15; j++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 1; i < 31; i++) {
                    if (i % 5 == 0) {
                        balancer.addTask(new Runnable() {
                            @Override
                            public void run() {
                                long count = 0;
                                while (count < 100_000_000L) {
                                    count++;
                                }
                            }
                        }, Priority.LOW);
                    } else {
                        balancer.addTask(new Runnable() {
                            @Override
                            public void run() {
//                                Lock lock = new ReentrantLock();
//                                lock.lock();
                                //do some task
//                                lock.unlock();
                            }
                        }, Priority.HIGH);
                    }
                }
            }
        }).start();

        balancer.executeTasks();
    }
}
