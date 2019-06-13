package balancer;

import java.util.concurrent.Executor;

public class CustExecutorService implements Executor {

    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }
}
