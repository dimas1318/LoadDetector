package balancer;

import balancer.serializer.Serializer;
import continuation.Priority;
import continuation.StatedContinuation;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class Balancer {

    private static final ContinuationScope SCOPE = new ContinuationScope("BALANCER_SCOPE");
    private static final int DEFAULT_N_THREADS = 2;
    private static final double DEFAULT_CPU_LOAD = 0.9D;
    private static final int MAX_ATTEMPTS = 100;

    private final ExecutorService executorService;
//        private final Executor executorService;
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
//        executorService = new CustExecutorService();
        isSerializationEnabled = enableSerialization;
        if (isSerializationEnabled) {
            taskNameQueue = new PriorityBlockingQueue<>(11, (p1, p2) -> p2.getPriority().compareTo(p1.getPriority()));
            serializer = new Serializer();
        } else {
            taskQueue = new PriorityBlockingQueue<>(11, new ContinuationStateComparator(false));
//            taskQueue = new LinkedList<>();
        }
        activeTasks = new PriorityBlockingQueue<>(11, new ContinuationStateComparator(true));

        loadChecker = new LoadChecker(limitCpu);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (loadChecker.isCpuOverloaded()) {
                    printInfo();
                    if (!activeTasks.isEmpty()) {
                        StatedContinuation task = activeTasks.poll();
//                        task.myPause(SCOPE);
//                        task.
                        task.myTryForceYield(task._getThread());
                        if (task.isPreempted()) {
                            System.out.println(task.getPriority() + " is yielded");
                            if (isSerializationEnabled) {
                                String taskName = Integer.toHexString(System.identityHashCode(task));
                                serializer.serialize(task, taskName);
                                taskNameQueue.add(new Pair(taskName, task.getPriority()));
                            } else {
                                taskQueue.add(task);
                            }
                        } else if (!task.isDone()) {
                            activeTasks.add(task);
                        }
                        printInfo();
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
            printInfo();
            if (!taskQueue.isEmpty()) {
                StatedContinuation task = taskQueue.poll();
                if (!task._getMounted()) {
                    synchronized (this) {
                        activeTasks.add(task);
                        executorService.execute(() -> {
                                    if (!task._getMounted()) {
                                        task.run();
                                        System.out.println(task.getPriority() + " " + (System.currentTimeMillis() - task.getTime()));
                                        activeTasks.remove(task);
                                        if (!task.isDone()) {
                                            taskQueue.add(task);
                                        }
                                    }
                                }
                        );
                    }
                    printInfo();
                } else if (!task.isDone()) {
                    taskQueue.add(task);
                }
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
        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdown();
        }
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

    private void printInfo() {
//        System.out.println("taskQueue: " + taskQueue);
//        System.out.println("activeTasks: " + activeTasks);
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


//    # To suppress the following error report, specify this argument
//# after -XX: or in .hotspotrc:  SuppressErrorAt=/continuation.cpp:2640
//            #
//            # A fatal error has been detected by the Java Runtime Environment:
//            #
//            #  Internal Error (/home/dmitry/Downloads/LOOM/loom/src/hotspot/share/runtime/continuation.cpp:2640), pid=4122, tid=4139
//            #  assert(cont != __null && oopDesc::is_oop_or_null(cont)) failed: Invalid cont: 0x0000000000000000
//            #
//            # JRE version: OpenJDK Runtime Environment (13.0) (slowdebug build 13-internal+0-adhoc.dmitry.loom)
//            # Java VM: OpenJDK 64-Bit Server VM (slowdebug 13-internal+0-adhoc.dmitry.loom, mixed mode, tiered, compressed oops, g1 gc, linux-amd64)
//# Problematic frame:
//            # V  [libjvm.so+0x8b01e3]  Continuation::freeze0(JavaThread*, FrameInfo*, bool)+0x37d
//            #
//            # Core dump will be written. Default location: Core dumps may be processed with "/usr/share/apport/apport %p %s %c %d %P" (or dumping to /home/dmitry/Downloads/LoadDetector/target/classes/core.4122)
//            #
//            # An error report file with more information is saved as:
//            # /home/dmitry/Downloads/LoadDetector/target/classes/hs_err_pid4122.log
//    Could not load hsdis-amd64.so; library not loadable; PrintAssembly is disabled
//    Compiled method (c1)    5722  368       2       java.lang.Thr
}
