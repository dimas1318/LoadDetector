import balancer.Balancer;
import continuation.Priority;

public class NewMain {

    public static void main(String[] args) {

        Balancer balancer = new Balancer(3, false, 0.05D);

        final var t1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("t1 LOW");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        final var t2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("t2 MEDIUM");
                while (true){}
            }
        };

        final var t3 = new Runnable() {
            @Override
            public void run() {
                System.out.println("t3 HIGH");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        final var t4 = new Runnable() {
            @Override
            public void run() {
                System.out.println("t4 HIGH");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        final var t5 = new Runnable() {
            @Override
            public void run() {
                System.out.println("t5 LOW");
            }
        };

        final var t6 = new Runnable() {
            @Override
            public void run() {
                System.out.println("t6 MEDIUM");
            }
        };

        balancer.addTask(t1, Priority.LOW);
        balancer.addTask(t2, Priority.MEDIUM);
        balancer.addTask(t3, Priority.HIGH);
        balancer.addTask(t4, Priority.HIGH);
        balancer.addTask(t5, Priority.LOW);
        balancer.addTask(t6, Priority.MEDIUM);
        balancer.executeTasks();
    }
}