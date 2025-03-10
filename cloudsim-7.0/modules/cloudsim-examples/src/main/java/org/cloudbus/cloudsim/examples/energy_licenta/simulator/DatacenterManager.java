package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.List;

public class DatacenterManager {

    public static Datacenter createDatacenter(String name, int numHosts) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new Pe(0, new PeProvisionerSimple(1000))); // 1 CPU core with 1000 MIPS

            int ram = 20480;
            long storage = 1000000;
            int bw = 10000;

            Host host = new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList));
            hostList.add(host);
        }
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = 0.01;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.01;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, timeZone, costPerSec, costPerMem, costPerStorage, costPerBw);
        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new ArrayList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
