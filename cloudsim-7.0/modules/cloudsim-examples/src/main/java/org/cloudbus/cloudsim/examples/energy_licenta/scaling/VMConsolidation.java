package org.cloudbus.cloudsim.examples.energy_licenta.scaling;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.List;

public class VMConsolidation {
    // =================== Consolidarea VM-urilor pentru reducerea consumului
    // =================== in loc sa folosim toate VM-urile de la inceput, incepem doar cu cate VM-uri sunt necesare si sa alocam mai multe doar daca e nevoie.
    public static void consolidateVMs(List<Vm> vmList, List<Cloudlet> cloudletList) {
        int activeVMs = Math.min(vmList.size(), (cloudletList.size() / 3) + 1); // Folosim doar VM-urile necesare

        for (int i = activeVMs; i < vmList.size(); i++) {
            vmList.get(i).setMips(0); // Oprire VM-uri neutilizate
            System.out.println(" ~!!!~ Consolidare: Oprire VM #" + vmList.get(i).getId());
        }
    }
}
