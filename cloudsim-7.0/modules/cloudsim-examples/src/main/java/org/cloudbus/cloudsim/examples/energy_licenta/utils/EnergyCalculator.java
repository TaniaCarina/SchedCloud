package org.cloudbus.cloudsim.examples.energy_licenta.utils;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class EnergyCalculator {

    //  Dynamic Voltage and Frequency Scaling (DVFS)
    //  DVFS permite ajustarea frecventei CPU pentru a reduce consumul de energie atunci cand VM-ul nu este folosit intens.
    //  se calc energia pt fiecare cloudlet
    //  energie (kJ) = execTime (sec) × MIPS × powerPerMIPS / 1000
    public static double calculateEnergyConsumption(Cloudlet cloudlet, Vm vm) {
        double executionTime = cloudlet.getActualCPUTime();

        // DVFS: Daca VM-ul are o utilizare CPU sub 50%, reducem frecventa
        double frequencyFactor = vm.getMips() > 250 ? 1.0 : 0.7;  // Daca MIPS < 250, frecventa e mai mica

        double powerPerMIPS = 0.5 * frequencyFactor;  // Reducem consumul energetic
        double energyKJ = executionTime * vm.getMips() * powerPerMIPS / 1000.0;
        return energyKJ;
    }
}
