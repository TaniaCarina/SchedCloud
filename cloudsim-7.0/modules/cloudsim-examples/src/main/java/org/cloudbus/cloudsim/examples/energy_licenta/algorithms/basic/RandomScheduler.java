package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.List;
import java.util.Random;

public class RandomScheduler implements SchedulingAlgorithm {

    private final Random random = new Random();

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList) {
        for (Cloudlet cloudlet : cloudletList) {
            int index = random.nextInt(vmList.size());
            Vm selectedVm = vmList.get(index);

            cloudlet.setVmId(selectedVm.getId());
            broker.bindCloudletToVm(cloudlet.getCloudletId(), selectedVm.getId());
        }

        System.out.println("Random Scheduler applied!");
    }
}