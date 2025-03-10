package org.cloudbus.cloudsim.examples.energy;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.List;

public class FCFS implements Algorithms.SchedulingAlgorithm {
    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmlist, List<Cloudlet> cloudletList) {
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);
            Vm vm = vmlist.get(i % vmlist.size());
            cloudlet.setVmId(vm.getId());
        }
        System.out.println("~~~ FCFS Algorithm applied! ~~~");
    }
}
