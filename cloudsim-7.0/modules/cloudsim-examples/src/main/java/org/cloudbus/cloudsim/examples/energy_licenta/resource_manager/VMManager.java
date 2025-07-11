package org.cloudbus.cloudsim.examples.energy_licenta.resource_manager;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;

import java.util.ArrayList;
import java.util.List;

public class VMManager {
    public static List<Vm> createVMs(int brokerId, int numVMs, int mips, int ram, long bw, long size, int pesNumber) {
        List<Vm> vmList = new ArrayList<>();

        for (int i = 0; i < numVMs; i++) {
            Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        System.out.println("[VM Creation] Created " + numVMs + " VM(s).");
        return vmList;
    }

    private static final int MIN_INITIAL_VMS = 5;

    public static List<Vm> createDynamicVMs(int brokerId, int numVMs, int mips, int ram, long bw, long size, int pesNumber) {
        List<Vm> vmList = new ArrayList<>();
        int initialVMs = Math.max(MIN_INITIAL_VMS, numVMs); // Folosim numarul exact de VM-uri specificat

        for (int i = 0; i < initialVMs; i++) {
            Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        System.out.println("[Scaling] Started with " + initialVMs + " VM(s).");
        return vmList;
    }


}
