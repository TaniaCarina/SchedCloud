package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.heuristic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.Comparator;
import java.util.List;


/**
 *    Sorteaza cloudletele dupa lungimea executiei (cel mai lung task primul)
 */

public class LJF implements SchedulingAlgorithm {

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList) {
        cloudletList.sort(Comparator.comparingLong(Cloudlet::getCloudletLength).reversed());

        int vmIndex = 0;
        for (Cloudlet cloudlet : cloudletList) {
            Vm vm = vmList.get(vmIndex);
            cloudlet.setVmId(vm.getId());
            broker.bindCloudletToVm(cloudlet.getCloudletId(), vm.getId());
            vmIndex = (vmIndex + 1) % vmList.size();
        }

        System.out.println("~~~ Longest Job First (LJF) Scheduler applied! ~~~");
    }
}