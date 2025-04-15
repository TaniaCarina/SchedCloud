package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.List;

public class RoundRobin implements SchedulingAlgorithm {

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList) {
        int vmIndex = 0;

        for (Cloudlet cloudlet : cloudletList) {
            Vm vm = vmList.get(vmIndex);
            cloudlet.setVmId(vm.getId());
            broker.bindCloudletToVm(cloudlet.getCloudletId(), vm.getId());

            vmIndex = (vmIndex + 1) % vmList.size(); // Selecteaza VM-ul în mod ciclic
        }

        System.out.println("~~~ Round Robin Scheduler applied! ~~~");
    }
}
