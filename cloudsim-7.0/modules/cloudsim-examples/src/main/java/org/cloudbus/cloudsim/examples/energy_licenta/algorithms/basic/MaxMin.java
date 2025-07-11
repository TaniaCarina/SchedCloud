package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.*;


/**
 * Cauta pentru fiecare cloudlet VM-ul pe care il finalizeaza cel mai repede (similar cu Min-Min).
 * Apoi alege cloudletul care are cel mai mare dintre timpii minimi de finalizare – adica cel mai "costisitor" cloudlet dintre cele care au variante rapide.
 * Il atribuie acelui VM si repeta procesul pana cand toate cloudlet-urile sunt programate.
 */

public class MaxMin implements SchedulingAlgorithm {

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList) {
        List<Cloudlet> unassignedCloudlets = new ArrayList<>(cloudletList);
        Map<Integer, Double> vmFinishTimes = new HashMap<>();

        for (Vm vm : vmList) {
            vmFinishTimes.put(vm.getId(), 0.0);
        }

        while (!unassignedCloudlets.isEmpty()) {
            Cloudlet selectedCloudlet = null;
            Vm selectedVm = null;
            double maxMinCompletionTime = -1;

            for (Cloudlet cloudlet : unassignedCloudlets) {
                double minCompletionTimeForCloudlet = Double.MAX_VALUE;
                Vm bestVmForCloudlet = null;

                for (Vm vm : vmList) {
                    double estTime = cloudlet.getCloudletLength() / vm.getMips();
                    double completionTime = vmFinishTimes.get(vm.getId()) + estTime;

                    if (completionTime < minCompletionTimeForCloudlet) {
                        minCompletionTimeForCloudlet = completionTime;
                        bestVmForCloudlet = vm;
                    }
                }

                if (minCompletionTimeForCloudlet > maxMinCompletionTime) {
                    maxMinCompletionTime = minCompletionTimeForCloudlet;
                    selectedCloudlet = cloudlet;
                    selectedVm = bestVmForCloudlet;
                }
            }

            if (selectedCloudlet != null && selectedVm != null) {
                broker.bindCloudletToVm(selectedCloudlet.getCloudletId(), selectedVm.getId());
                double execTime = selectedCloudlet.getCloudletLength() / selectedVm.getMips();
                vmFinishTimes.put(selectedVm.getId(), vmFinishTimes.get(selectedVm.getId()) + execTime);
                unassignedCloudlets.remove(selectedCloudlet);
            } else {
                break;
            }
        }

        System.out.println("Max-Min Scheduler applied!");
    }
}
