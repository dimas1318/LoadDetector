public class Main {

    public static void main(String[] args) {

        var SCOPE = new ContinuationScope("SCOPE");
        var continuation = new Continuation(SCOPE, () -> {
            System.out.println("first");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("second");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("third");
        });

        var slave = new Thread(continuation::run);

        var host = new Thread(() -> {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }

            Continuation.yield(slave, SCOPE);

//            System.out.println("preempt");
//            var status = continuation.tryPreempt(slave);
//            System.out.println("preempt status " + status);

//            for (var trace : continuation.getStackTrace()) {
//                System.out.println(trace.toString());
//            }

            System.out.println("isDone: " + continuation.isDone());
            System.out.println("isPreempted: " + continuation.isPreempted());

            System.out.println(slave.getState());
//            slave.start();

            System.out.println(continuation.toString());
        });
        host.start();
        slave.start();

//        System.out.println("it works !");
    }
}
