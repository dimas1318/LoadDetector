public class Stepper implements Runnable {

    private static final ContinuationScope SCOPE = new ContinuationScope() {
    };

    private final int maxDepth;

    private Stepper(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void run() {
        run1(maxDepth);
    }

    private void run1(int depth) {
        System.out.println(depth);
        if (depth > 0) {
            run1(depth - 1);
            System.out.println("before yield");
            Continuation.yield(SCOPE);
        }
    }

    public static Continuation continuation(int maxDepth) {
        Runnable task = new Stepper(maxDepth);
        return new Continuation(SCOPE, task);
    }
}
