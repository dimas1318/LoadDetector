import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class LoadChecker {

    private final OperatingSystemMXBean osBean;

    public LoadChecker() {
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    public double getLoadFactor() {
        return osBean.getProcessCpuLoad();
    }
}
