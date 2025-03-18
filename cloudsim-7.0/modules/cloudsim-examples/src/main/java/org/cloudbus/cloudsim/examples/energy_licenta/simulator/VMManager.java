package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

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
        System.out.println(" ~!!!~ [VM Creation] Created " + numVMs + " VM(s).");
        return vmList;
    }

    private static final int MIN_INITIAL_VMS = 5;

    public static List<Vm> createDynamicVMs(int brokerId, int numVMs, int mips, int ram, long bw, long size, int pesNumber) {
        List<Vm> vmList = new ArrayList<>();
        int initialVMs = Math.max(MIN_INITIAL_VMS, numVMs); // Folosim numărul exact de VM-uri specificat

        for (int i = 0; i < initialVMs; i++) {
            Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        System.out.println(" ~!!!~ [Scaling] Started with " + initialVMs + " VM(s).");
        return vmList;
    }


    //////////////////// SCALARE DINAMICA LA VM-URI ///////////////////////


    /**
     * started with 20 VMs, the system automatically consolidated (shut down) VMs 17, 18, and 19 because they were not needed.
     *  RoundRobin
     *  Before: ~361 kWh   with MIN_INITIAL_VMS = 5  consolidated 0
     *  After: ~156 kWh    with MIN_INITIAL_VMS = 20 consolidated 3
     *  After: ~86  kWh    with MIN_INITIAL_VMS = 30 consolidated 13
     *  After: ~13  kWh    with MIN_INITIAL_VMS = 60 consolidated 43  trebuie alocate mai multe resurse, apar unele erori
     *  Daca creezi 60 de VM-uri, ultimele 20 vor esua, pentru ca nu exista suficient spatiu pe host-uri
     *  Daca resursele (RAM, CPU, Bandwidth) sunt deja consumate de primele VM-uri, nu se mai pot aloca altele
     *
     *
     *
     *  !!!!  More initial VMs can reduce execution time, lowering energy use.
     *  Consolidation + fast execution = extreme energy efficiency
     *  Higher initial VMs lead to better parallel execution
     */
    //private static final int MIN_INITIAL_VMS = 40;  //


//    public static List<Vm> createDynamicVMs(int brokerId, int numCloudlets) {
//        List<Vm> vmList = new ArrayList<>();
//        int initialVMs = Math.max(MIN_INITIAL_VMS, numCloudlets / 10);
//
//        for (int i = 0; i < initialVMs; i++) {
//            int mips = 500;
//            int ram = 2048;
//            long bw = 1000;
//            long size = 10000;
//            Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
//            vmList.add(vm);
//        }
//        System.out.println(" ~!!!~ [Scaling] Started with " + initialVMs + " VM(s).");
//        return vmList;
//    }



}
