package org.cloudbus.cloudsim.examples.energy_licenta.algorithms;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import java.util.List;

public class RoundRobin implements SchedulingAlgorithm {

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList){
        cloudletList.sort((c1, c2) -> Long.compare(c1.getCloudletLength(), c2.getCloudletLength())); // Sortare dupa lungime cloudlet-urile

        int vmIndex = 0; // Folosit pentru a alterna VM-urile
        for (Cloudlet cloudlet : cloudletList) {
            Vm assignedVm = vmList.get(vmIndex % vmList.size()); // Selecteaza VM-ul în mod ciclic
            cloudlet.setVmId(assignedVm.getId()); // Atribuie Cloudlet-ul la VM-ul selectat
            vmIndex++; // Trecem la urmatorul VM pentru urmatorul Cloudlet
        }
        System.out.println("~~~ RoundRobin Algorithm applied! ~~~");
    }
}
