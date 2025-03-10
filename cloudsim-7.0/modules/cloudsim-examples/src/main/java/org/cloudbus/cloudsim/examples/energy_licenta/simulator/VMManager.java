package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;

import java.util.ArrayList;
import java.util.List;

public class VMManager {
    private static List<Vm> createVMs(int brokerId, int numVMs) {
        List<Vm> vmList = new ArrayList<>();
        for (int i = 0; i < numVMs; i++) {
            int mips = 500;
            int ram = 2048;
            long bw = 1000;
            long size = 10000;
            Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        return vmList;
    }


    //////////////////// SCALARE DINAMICA LA VM-URI ///////////////////////

    public static List<Vm> createDynamicVMs(int brokerId, int numCloudlets) {
        List<Vm> vmList = new ArrayList<>();
        int initialVMs = Math.max(10, numCloudlets /10); // incepem cu un minim de 5 VM-uri sau 1 VM la fiecare 5 cloudlet-uri

        for (int i = 0; i < initialVMs; i++) {
            int mips = 500;
            int ram = 2048;
            long bw = 1000;
            long size = 10000;
            Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        System.out.println(" ~!!!~ [Scaling] Pornite initial " + initialVMs + " VM-uri.");
        return vmList;
    }


}
