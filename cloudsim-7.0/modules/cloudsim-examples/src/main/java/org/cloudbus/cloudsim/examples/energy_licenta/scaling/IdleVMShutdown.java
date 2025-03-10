package org.cloudbus.cloudsim.examples.energy_licenta.scaling;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdleVMShutdown {


    // ===================== Oprirea VM-urilor Idle pentru economisirea energiei
    // ===================== Daca un VM nu are cloudlet-uri alocate, il oprim pentru a reduce consumul.
    private static final double IDLE_THRESHOLD = 5.0; // Shut down VMs after 5 units of idle time
    private static Map<Integer, Double> lastBusyTimeMap = new HashMap<>();

    public static void shutdownIdleVMs(List<Vm> vmList, List<Cloudlet> cloudletList) {
        if (CloudSim.clock() > 0) return; // Don't shut down after simulation ends

        int shutdownCount = 0;
        for (Vm vm : vmList) {
            boolean isUsed = cloudletList.stream().anyMatch(cloudlet -> cloudlet.getVmId() == vm.getId());
            double lastBusyTime = lastBusyTimeMap.getOrDefault(vm.getId(), 0.0);
            double idleTime = CloudSim.clock() - lastBusyTime;

            if (!isUsed && idleTime > IDLE_THRESHOLD) {
                System.out.println(" ~!!!~ [Scaling] Shutting down VM #" + vm.getId() + " (Idle Time: " + idleTime + "s).");
                vm.setMips(0);
                shutdownCount++;
            }
        }
        if (shutdownCount == 0) {
            System.out.println(" ~!!!~ [Scaling] No idle VMs to shut down.");
        }
    }

}
