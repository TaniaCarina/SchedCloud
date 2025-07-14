package org.cloudbus.cloudsim.examples.energy_licenta.scaling;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.ArrayList;
import java.util.List;

public class VMConsolidation {

    public static int turnedOffVMs = 0;

    public static void consolidateVMs(List<Vm> vmList, List<Cloudlet> cloudletList) {
        int activeVMs = Math.min(vmList.size(), (cloudletList.size() / 3) + 1);

        for (int i = activeVMs; i < vmList.size(); i++) {
            vmList.get(i).setMips(0); // Oprire VM-uri neutilizate
            System.out.println("Consolidare: Oprire VM #" + vmList.get(i).getId());
        }

        int totalVMs = vmList.size();
        int turnedOff = 0;
        for (Vm vm : vmList) {
            if (vm.getMips() == 0) turnedOff++;
        }
        System.out.println("Consolidare aplicata: " + turnedOff + "/" + totalVMs + " VM-uri oprite.");

    }

    public static void consolidateVMs2(List<Vm> vmList, List<Cloudlet> cloudletList) {
        int activeVMs = Math.min(vmList.size(), (cloudletList.size() / 3) + 1);

        for (int i = activeVMs; i < vmList.size(); i++) {
            vmList.get(i).setMips(0); // Oprire VM-uri neutilizate
            System.out.println("Consolidare: Oprire VM #" + vmList.get(i).getId());
        }

        int totalVMs = vmList.size();
        int turnedOff = 0;
        for (Vm vm : vmList) {
            if (vm.getMips() == 0) turnedOff++;
        }
        turnedOffVMs = turnedOff;  // salvează în variabila statică
        System.out.println("Consolidare aplicata: " + turnedOff + "/" + totalVMs + " VM-uri oprite.");
    }

    /**
     *   Consolidare falsa – doar simuleaza logica, nu modifica nimic (pentru salvarea hostID-ului)
     *   daca se schimba o proprietate esentiala unui VM, se detecteaza modificarea si reevalueaza
     *   pozitionarea VM-urilor in host-uri si se salveaza hostul asociat VM-ului, altfel le sterge
     */

    public static void fakeConsolidateVMs(List<Vm> vmList, List<Cloudlet> cloudletList) {

        int activeVMs = vmList.size() - 1 ;
        for (int i = activeVMs; i < vmList.size(); i++) {
            vmList.get(i).setMips(0);
        }

    }

    }



