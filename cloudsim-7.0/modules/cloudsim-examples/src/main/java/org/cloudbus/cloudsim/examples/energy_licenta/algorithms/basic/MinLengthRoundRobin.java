package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.Comparator;
import java.util.List;


/**
 * Min-Min combined with RoundRobin
 */

public class MinLengthRoundRobin implements SchedulingAlgorithm {

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList){
        cloudletList.sort(Comparator.comparingLong(Cloudlet::getCloudletLength));

        int vmIndex = 0;
        for (Cloudlet cloudlet : cloudletList) {
            Vm assignedVm = vmList.get(vmIndex % vmList.size());
           // cloudlet.setVmId(assignedVm.getId());
            broker.bindCloudletToVm(cloudlet.getCloudletId(), assignedVm.getId());
            vmIndex++;
        }
        System.out.println("MinLengthRoundRobin Algorithm applied!");
    }
}
