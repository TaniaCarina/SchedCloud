package org.cloudbus.cloudsim.examples.energy_licenta.resource_manager;

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

    public static Datacenter createDatacenterNormal(String name, int numHosts, int hostMIPS, int hostRAM) {
        List<Host> hostList = new ArrayList<>();

        for (int i = 0; i < numHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new Pe(i, new PeProvisionerSimple(hostMIPS)));

            Host host = new Host(
                    i,
                    new RamProvisionerSimple(hostRAM),
                    new BwProvisionerSimple(10000),
                    1000000,
                    peList,
                    new VmSchedulerTimeShared(peList)
            );

            hostList.add(host);
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList,
                10.0, 3.0, 0.05, 0.1, 0.1
        );

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }


}
