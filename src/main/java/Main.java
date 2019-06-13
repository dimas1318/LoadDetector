public class Main {

    static class Control {
        public volatile boolean flag = false;
    }

    public static void main(String[] args) {

        final Control control = new Control();

        var SCOPE = new ContinuationScope("SCOPE");
        var continuation = new Continuation(SCOPE, new Runnable() {
            @Override
            public void run() {
//                while (!Thread.interrupted()) {
//                    slaveTask();
//                }
                long count = 0;
                while (count < 100_000_000L) {
                    if (count++ % 10_000_000 == 0) {
                        System.out.println(count);
                    }
                }
//                System.out.println("cont is finished");
//                slaveTask();
            }
        });

//        Fiber slave = Fiber.schedule(continuation::run);
//        Fiber slave = Fiber.schedule((Runnable) () -> {
//            while (true){}
//        });
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

        System.out.println("slave: " + slave.getId() + " " + slave.getName());
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
//
//        Fiber host = Fiber.schedule(() -> {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new AssertionError(e);
//            }
//            while (!slave.isCancelled()) slave.myYield();
////            slave.myYield();
////            Thread.yield();
//            System.out.println("HOST is alive");
//        });
//
//        slave.join();
//        host.join();

//        slave.myYield();

        var host = new Thread(() -> {
////            slave.mySetContinuation(continuation);
////            Continuation cont = slave.myGetContinuation();
////
////            slave.start();
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }

            continuation.myTryForceYield(continuation._getThread());
//            Continuation.myYield(slave, continuation._getContinuationScope());

            System.out.println("preempted: " + continuation.isPreempted());
            System.out.println("done: " + continuation.isDone());

//            finish[0] = true;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            continuation.run();
//
//            slave.myYield();
////            Thread.yield();
////            Continuation.myYield(slave, SCOPE);
//
//            slave.interrupt();
            System.out.println("HOST is alive");
//
////            slave.myGetContinuation().myTryForceYield(slave);
////            slave.yieldContinuation();
////            continuation.myPause(SCOPE);
////            Continuation.yield(slave, SCOPE);
////            continuation.pause(continuation.myGetScope());
////
////            slave.interrupt();
////            System.out.println("preempt");
////            var status = continuation.tryPreempt(slave);
////            System.out.println("preempt status " + status);
//
////            for (var trace : continuation.getStackTrace()) {
//////                System.out.println(trace.toString());
//////            }
//////            slave.yieldContinuation();
////
////            System.out.println("isDone: " + cont.isDone());
////            System.out.println("isPreempted: " + cont.isPreempted());
////
////            System.out.println(slave.getState());
////
////            System.out.println(cont.toString());
////            continuation.run();
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
        host.start();
//
        System.out.println("host: " + host.getId() + " " + host.getName());

//        System.out.println("it works !");
    }

    private static void slaveTask() {
        //            while (true) {
//                System.out.println("kkk");
//            }
//            Timer timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    System.out.println("out :" + "1");
//                }
//            }, 500, 500);
        System.out.println("first");
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            Continuation.yield(SCOPE);
        System.out.println("second");
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            Continuation.yield(SCOPE);
        System.out.println("third");
    }
}
