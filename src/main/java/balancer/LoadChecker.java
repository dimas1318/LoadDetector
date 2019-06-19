package balancer;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class LoadChecker {

    private final OperatingSystemMXBean osBean;
    private final double limitCpuLoad;

    public LoadChecker(double limitCpu) {
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        limitCpuLoad = limitCpu;
    }

    public boolean isCpuOverloaded() {
        double processCpuLoad = osBean.getProcessCpuLoad();
//        System.out.println(processCpuLoad);
        return processCpuLoad > limitCpuLoad;
//        return false;
    }
}
