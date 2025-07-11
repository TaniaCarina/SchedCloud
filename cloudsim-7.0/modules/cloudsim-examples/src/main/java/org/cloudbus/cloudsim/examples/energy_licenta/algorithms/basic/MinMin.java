package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.*;


/**
 * Calculeaza pentru fiecare cloudlet si fiecare VM timpul estimat de finalizare
 * Alege perechea cloudlet-VM cu cel mai mic timp de finalizare
 * Actualizeaza starea VM-ului si repeta procesul pana cand toate cloudlet-urile sunt alocate
 */

public class MinMin implements SchedulingAlgorithm {

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList) {
        List<Cloudlet> unassignedCloudlets = new ArrayList<>(cloudletList);
        Map<Integer, Double> vmFinishTimes = new HashMap<>();

        for (Vm vm : vmList) {
            vmFinishTimes.put(vm.getId(), 0.0);
        }

        while (!unassignedCloudlets.isEmpty()) {
            Cloudlet bestCloudlet = null;
            Vm bestVm = null;
            double minCompletionTime = Double.MAX_VALUE;

            for (Cloudlet cloudlet : unassignedCloudlets) {
                for (Vm vm : vmList) {
                    double estTime = cloudlet.getCloudletLength() / (double) vm.getMips();
                    double completionTime = vmFinishTimes.get(vm.getId()) + estTime;

                    if (completionTime < minCompletionTime) {
                        minCompletionTime = completionTime;
                        bestCloudlet = cloudlet;
                        bestVm = vm;
                    }
                }
            }

            if (bestCloudlet != null && bestVm != null) {
                broker.bindCloudletToVm(bestCloudlet.getCloudletId(), bestVm.getId());
                double execTime = bestCloudlet.getCloudletLength() / (double) bestVm.getMips();
                vmFinishTimes.put(bestVm.getId(), vmFinishTimes.get(bestVm.getId()) + execTime);
                unassignedCloudlets.remove(bestCloudlet);
            } else {
                break;
            }
        }

        System.out.println("~~~ Min-Min Scheduler applied! ~~~");
    }
}