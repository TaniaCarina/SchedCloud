package org.cloudbus.cloudsim.examples.energy_licenta.resource_manager;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.HostEntity;
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
                        new RamProvisionerSimple(hostRAM),
                        new BwProvisionerSimple(10000),
                        1000000,
                        peList,
                        new VmSchedulerTimeShared(peList)
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
    public static void bindVMsToHosts(List<Vm> vmList, Datacenter datacenter) {
        if (datacenter == null || vmList == null) return;

        for (Vm vm : vmList) {
            Host host = datacenter.getVmAllocationPolicy().getHost(vm);
            if (host != null) {
                vm.setHost(host); // Seteaza explicit hostul in VM
            }
        }
    }


}
