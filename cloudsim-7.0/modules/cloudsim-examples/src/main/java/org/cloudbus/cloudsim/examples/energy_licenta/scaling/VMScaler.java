package org.cloudbus.cloudsim.examples.energy_licenta.scaling;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.List;

public class VMScaler {

    private static final double SCALE_UP_THRESHOLD = 0.8;  // Scale up only if CPU > 80%
    private static final double SCALE_DOWN_THRESHOLD = 0.3;  // Shut down VMs if CPU < 30%
    private static final int SCALING_DELAY = 5;  // Prevents frequent scaling

    private static double lastScaleTime = 0;

    public static void scaleUpVMs(DatacenterBroker broker, List<Vm> vmList, int maxVMs) {
        if (CloudSim.clock() - lastScaleTime < SCALING_DELAY) return; // Cooldown active, skip scaling

        int activeVMs = vmList.size();
        int waitingCloudlets = (int) broker.getCloudletSubmittedList().stream()
                .filter(cloudlet -> !cloudlet.isFinished())
                .count();

        double avgCpuUtilization = calculateAverageCPUUsage(vmList, broker);

        if (avgCpuUtilization > SCALE_UP_THRESHOLD && waitingCloudlets > activeVMs) {
            int newVMs = Math.min(3, maxVMs - activeVMs);

            for (int i = 0; i < newVMs; i++) {
                int newId = activeVMs + i;
                Vm vm = new Vm(newId, broker.getId(), 500, 1, 2048, 1000, 10000, "Xen", new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }
            lastScaleTime = CloudSim.clock();
            System.out.println(" ~!!!~ [Scaling] Added " + newVMs + " new VM(s).");
        }
    }

    ////consumul de energie este calculat pe baza utilizarii cpu a fiecarui vm
    private static double calculateAverageCPUUsage(List<Vm> vmList, DatacenterBroker broker) {
        if (vmList.isEmpty()) return 0.0;
        double totalUsage = 0.0;
        int count = 0;

        for (Vm vm : vmList) {
            double vmUsage = getVmUtilization(vm, broker);
            totalUsage += vmUsage;
            count++;
        }
        return (count == 0) ? 0.0 : totalUsage / count;
    }

    private static double getVmUtilization(Vm vm, DatacenterBroker broker) {
        List<Cloudlet> cloudlets = broker.getCloudletSubmittedList();
        double totalUtilization = 0.0;

        for (Cloudlet cloudlet : cloudlets) {
            if (cloudlet.getVmId() == vm.getId() && !cloudlet.isFinished()) {
                totalUtilization += cloudlet.getUtilizationOfCpu(CloudSim.clock());
            }
        }
        return totalUtilization;
    }





    public static void scaleUpVMs_nu_prea_bun(DatacenterBroker broker, List<Vm> vmList, int maxVMs) {    //consum de energie dublu cu alocare dinamica
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
