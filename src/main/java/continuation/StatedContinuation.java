package continuation;

public class StatedContinuation extends Continuation {

    private Priority priority;

    private long time;

    public StatedContinuation(ContinuationScope scope, Runnable target) {
        super(scope, target);
        priority = Priority.MEDIUM;
    }

    public StatedContinuation(ContinuationScope scope, int stackSize, Runnable target) {
        super(scope, stackSize, target);
        priority = Priority.MEDIUM;
    }

    public StatedContinuation(ContinuationScope scope, Runnable target, Priority priority) {
        super(scope, target);
        this.priority = priority;
        time = System.currentTimeMillis();
    }

    public StatedContinuation(ContinuationScope scope, int stackSize, Runnable target, Priority priority) {
        super(scope, stackSize, target);
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getTime() {
        return time;
    }
}
