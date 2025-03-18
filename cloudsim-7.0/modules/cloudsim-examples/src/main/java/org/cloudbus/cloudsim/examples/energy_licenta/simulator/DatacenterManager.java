package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatacenterManager {
        public static Datacenter createDatacenter(String name, int numHosts, int hostMIPS, int hostRAM) {
            List<Host> hostList = new ArrayList<>();

            for (int i = 0; i < numHosts; i++) {
                List<Pe> peList = new ArrayList<>();
                peList.add(new Pe(0, new PeProvisionerSimple(hostMIPS))); // Fiecare Host are un CPU cu MIPS definit

                hostList.add(new Host(
                        i,
                        new RamProvisionerSimple(hostRAM), // RAM suficient pentru VM-uri
                        new BwProvisionerSimple(10000), // Bandwidth mare pentru a evita blocajele
                        1000000, // Storage mare pentru a putea rula multe VM-uri
                        peList,
                        new VmSchedulerTimeShared(peList) // Permite împărțirea resurselor între VM-uri
                ));
            }

            DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                    "x86", "Linux", "Xen",
                    hostList, 10.0, 3.0, 0.05, 0.1, 0.1);

            try {
                return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    public static Datacenter createDatacenterOLD(String name, int numHosts) {
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

    public static Datacenter createDatacenter_normal(String name, int numHosts) {
        List<Host> hostList = new ArrayList<>();

        for (int i = 0; i < numHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new Pe(0, new PeProvisionerSimple(5000))); // 5000 MIPS per core pentru a suporta mai multe VM-uri

            int ram = 20480;  // 🔹 32GB RAM per host (în loc de 20GB)
            long storage = 1000000; // 1TB Storage
            int bw = 10000; // 🔹 Creștem BW pentru a suporta mai multe VM-uri

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
