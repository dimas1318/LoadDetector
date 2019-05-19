import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {

        var SCOPE = new ContinuationScope("SCOPE");
        var continuation = new Continuation(SCOPE, () -> {
            while (true) {
//                System.out.println("kkk");
            }
//            Timer timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    System.out.println("out :" + "1");
//                }
//            }, 500, 500);
//            System.out.println("first");
//            try {
//                Thread.sleep(700);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
////            Continuation.yield(SCOPE);
//            System.out.println("second");
//            try {
//                Thread.sleep(700);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
////            Continuation.yield(SCOPE);
//            System.out.println("third");
        });

//        Fiber slave = Fiber.schedule(continuation::run);
//
//        Fiber host = Fiber.schedule(() -> {
//            try {
//                Thread.sleep(1_000);
//            } catch (InterruptedException e) {
//                throw new AssertionError(e);
//            }
//            System.out.println(slave.isAlive());
//            slave.cancel();
//        });
//
//        slave.join();
//        host.join();

        var slave = new Thread(continuation::run);
        slave.mySetContinuation(continuation);
        slave.start();
//
//
//        Continuation cont = slave.myGetContinuation();
//        System.out.println(cont.toString());
//        System.out.println("isDone: " + cont.isDone());
//        var status = cont.tryPreempt(slave);
//        System.out.println("preempt status " + status);
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new AssertionError(e);
//        }
//
//        status = cont.tryPreempt(slave);
//        System.out.println("preempt status " + status);
//
//        cont.run();

        var host = new Thread(() -> {
//            slave.mySetContinuation(continuation);
//            Continuation cont = slave.myGetContinuation();
//
//            slave.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }

            slave.myGetContinuation().pause(SCOPE);
//            slave.yieldContinuation();
//            Continuation.yield(cont);
//            Continuation.yield(slave, SCOPE);
//            continuation.pause(continuation.myGetScope());
//
////            System.out.println("preempt");
////            var status = continuation.tryPreempt(slave);
////            System.out.println("preempt status " + status);
//
////            for (var trace : continuation.getStackTrace()) {
////                System.out.println(trace.toString());
////            }
////            slave.yieldContinuation();
//
//            System.out.println("isDone: " + cont.isDone());
//            System.out.println("isPreempted: " + cont.isPreempted());
//
//            System.out.println(slave.getState());
//
//            System.out.println(cont.toString());
//            continuation.run();
        });
        host.start();

//        System.out.println("it works !");
    }
}
