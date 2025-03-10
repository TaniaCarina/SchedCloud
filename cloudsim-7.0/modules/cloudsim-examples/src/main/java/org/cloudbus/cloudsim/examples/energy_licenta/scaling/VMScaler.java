package org.cloudbus.cloudsim.examples.energy_licenta.scaling;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.List;

public class VMScaler {

    public static void scaleUpVMs(DatacenterBroker broker, List<Vm> vmList, int maxVMs) {
        if (CloudSim.clock() > 0) { // Daca simularea s-a terminat, nu mai scalam
            return;
        }

        int activeVMs = vmList.size();
        int waitingCloudlets = (int) broker.getCloudletSubmittedList().stream()
                .filter(cloudlet -> !cloudlet.isFinished())
                .count();

        if (waitingCloudlets > activeVMs * 2 && activeVMs < maxVMs) {
            int newVMs = Math.min(5, maxVMs - activeVMs);

            for (int i = 0; i < newVMs; i++) {
                int newId = activeVMs + i;
                Vm vm = new Vm(newId, broker.getId(), 500, 1, 2048, 1000, 10000, "Xen", new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }
            System.out.println(" ~!!!~ [Scaling] Adaugate " + newVMs + " VM-uri suplimentare.");
        }
    }
}
