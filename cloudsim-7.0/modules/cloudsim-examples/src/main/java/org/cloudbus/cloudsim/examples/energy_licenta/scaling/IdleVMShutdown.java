package org.cloudbus.cloudsim.examples.energy_licenta.scaling;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.List;

public class IdleVMShutdown {


    // ===================== Oprirea VM-urilor Idle pentru economisirea energiei
    // ===================== Daca un VM nu are cloudlet-uri alocate, il oprim pentru a reduce consumul.
    public static void shutdownIdleVMs(List<Vm> vmList, List<Cloudlet> cloudletList) {
        if (CloudSim.clock() > 0) { // Daca simularea s-a terminat, nu mai oprim VM-uri
            return;
        }

        int vmCount = 0;
        for (Vm vm : vmList) {
            boolean isUsed = cloudletList.stream().anyMatch(cloudlet -> cloudlet.getVmId() == vm.getId());
            if (!isUsed) {
                System.out.println(" ~!!!~  [Scaling] Oprire VM #" + vm.getId() + " pentru a economisi energie.");
                vm.setMips(0); // Simulam oprirea VM-ului
                vmCount++;
            }
        }
        if (vmCount == 0) {
            System.out.println(" ~!!!~  [Scaling] Toate VM-urile active sunt utilizate, nu s-au oprit VM-uri.");
        }
    }

}
