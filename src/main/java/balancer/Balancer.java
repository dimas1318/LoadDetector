package balancer;

import balancer.serializer.Serializer;
import continuation.Priority;
import continuation.StatedContinuation;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class Balancer {

    private static final ContinuationScope SCOPE = new ContinuationScope("BALANCER_SCOPE");
    private static final int DEFAULT_N_THREADS = 2;
    private static final double DEFAULT_CPU_LOAD = 0.9D;
    private static final int MAX_ATTEMPTS = 5;

    private final ExecutorService executorService;
    private final Queue<StatedContinuation> activeTasks;
    private final LoadChecker loadChecker;

    private Queue<StatedContinuation> taskQueue;
    private Queue<Pair> taskNameQueue;
    private Serializer serializer;

    private boolean isSerializationEnabled;

    private int attempts = 0;

    public Balancer() {
        this(DEFAULT_N_THREADS);
    }

    public Balancer(int nThreads) {
        this(nThreads, false, DEFAULT_CPU_LOAD);
    }

    public Balancer(boolean enableSerialization) {
        this(DEFAULT_N_THREADS, enableSerialization, DEFAULT_CPU_LOAD);
    }

    public Balancer(int nThreads, boolean enableSerialization, double limitCpu) {
        executorService = Executors.newFixedThreadPool(nThreads);
        isSerializationEnabled = enableSerialization;
        if (isSerializationEnabled) {
            taskNameQueue = new PriorityBlockingQueue<>(11, (p1, p2) -> p2.getPriority().compareTo(p1.getPriority()));
            serializer = new Serializer();
        } else {
            taskQueue = new PriorityBlockingQueue<>(11, new ContinuationStateComparator(false));
        }
        activeTasks = new PriorityBlockingQueue<>(11, new ContinuationStateComparator(true));

        loadChecker = new LoadChecker(limitCpu);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (loadChecker.isCpuOverloaded()) {
                    if (!activeTasks.isEmpty()) {
                        StatedContinuation task = activeTasks.poll();
                        task.myPause(SCOPE);
                        if (isSerializationEnabled) {
                            String taskName = Integer.toHexString(System.identityHashCode(task));
                            serializer.serialize(task, taskName);
                            taskNameQueue.add(new Pair(taskName, task.getPriority()));
                        } else {
                            taskQueue.add(task);
                        }
                    }
                }
            }
        }, 0, 1000);
    }

    public void addTask(Runnable target, Priority priority) {
        StatedContinuation task = new StatedContinuation(SCOPE, target, priority);
        addTask(task);
    }

    public void addTask(Runnable target) {
        StatedContinuation task = new StatedContinuation(SCOPE, target);
        addTask(task);
    }

    public void executeTasks() {
        if (isSerializationEnabled) {
            if (!taskNameQueue.isEmpty()) {
                String taskName = taskNameQueue.poll().getTaskName();
                executorService.execute(() -> {
                    StatedContinuation task = serializer.deserialize(taskName);
                    activeTasks.add(task);
                    task.run();
                    activeTasks.remove(task);
                    if (!task.isDone()) {
                        serializer.serialize(task, taskName);
                        taskNameQueue.add(new Pair(taskName, task.getPriority()));
                    }
                });
            } else {
                if (!sleep()) {
                    shutdown();
                    return;
                }
            }
        } else {
            if (!taskQueue.isEmpty()) {
                StatedContinuation task = taskQueue.poll();
                executorService.submit(() -> {
                    activeTasks.add(task);
                    task.run();
                    activeTasks.remove(task);
                    if (!task.isDone()) {
                        taskQueue.add(task);
                    }
                });
            } else {
                if (!sleep()) {
                    shutdown();
                    return;
                }
            }
        }
        executeTasks();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private void addTask(StatedContinuation task) {
        if (isSerializationEnabled) {
            String taskName = Integer.toHexString(System.identityHashCode(task));
            serializer.serialize(task, taskName);
            Pair taskNamePair = new Pair(taskName, task.getPriority());
            taskNameQueue.add(taskNamePair);
        } else {
            taskQueue.add(task);
        }
    }

    private boolean sleep() {
        if (attempts < MAX_ATTEMPTS) {
            attempts++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

//    c4, phase 1, HIGH
//    c5, phase 1, HIGH
//    c4, phase 2, HIGH
//    c5, phase 2, MEDIUM
//    c6, phase 1, MEDIUM
//    c6, phase 2, HIGH
//    c3, phase 1, MEDIUM
//    c2, phase 1, MEDIUM
//    c2, phase 2, MEDIUM
//    c5, phase 3, LOW
//    c1, phase 1, LOW
//    c1, phase 2, HIGH
//    c4, phase 3, LOW
//    c3, phase 2, LOW
//    c3, phase 3, HIGH
}
