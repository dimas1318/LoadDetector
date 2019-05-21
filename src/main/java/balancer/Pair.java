package balancer;

import continuation.Priority;

public class Pair {

    private String taskName;
    private Priority priority;

    public Pair(String taskName, Priority priority) {
        this.taskName = taskName;
        this.priority = priority;
    }

    public String getTaskName() {
        return taskName;
    }

    public Priority getPriority() {
        return priority;
    }
}
