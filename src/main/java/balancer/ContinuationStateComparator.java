package balancer;

import continuation.StatedContinuation;

import java.util.Comparator;

public class ContinuationStateComparator implements Comparator<StatedContinuation> {

    private final boolean inverse;

    public ContinuationStateComparator(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public int compare(StatedContinuation c1, StatedContinuation c2) {
        if (inverse) {
            return c1.getPriority().compareTo(c2.getPriority());
        } else {
            return c2.getPriority().compareTo(c1.getPriority());
        }
    }
}
