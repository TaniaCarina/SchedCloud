package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.List;

import static org.cloudbus.cloudsim.examples.energy_licenta.utils.EnergyCalculator.calculateEnergyConsumption;

public class ResultsPrinter {

    public static void printResults(DatacenterBroker broker, List<Vm> vmList, SchedulingAlgorithm algorithm) {
        List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
        double totalEnergyConsumption = 0.0;

        // Header formatat corect
        System.out.printf("%-12s %-10s %-5s %-12s %-12s %-12s %-18s%n",
                "CloudletID", "Status", "VM", "Start Time", "Finish Time", "Exec Time", "Energy Consumption");
        System.out.println("------------------------------------------------------------------------------------------");

        // Date formatate cu padding fix
        for (Cloudlet cloudlet : cloudletList) {
            // Gasim VM-ul asociat cloudlet-ului
            Vm assignedVm = null;
            for (Vm vm : vmList) {
                if (vm.getId() == cloudlet.getVmId()) {
                    assignedVm = vm;
                    break;
                }
            }

            // Calculam consumul de energie doar daca gasim VM-ul asociat
            double energyConsumption = (assignedVm != null) ? calculateEnergyConsumption(cloudlet, assignedVm) : 0.0;
            totalEnergyConsumption += energyConsumption;

            System.out.printf("%-12d %-10s %-5d %-12.2f %-12.2f %-12.2f %-18.2f%n",
                    cloudlet.getCloudletId(),
                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime(),
                    cloudlet.getActualCPUTime(),
                    energyConsumption);
        }

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("Algorithm Used: %s%n", algorithm.getClass().getSimpleName());
        System.out.printf("TOTAL ENERGY CONSUMPTION: %.2f kWh%n", totalEnergyConsumption);
    }
}
