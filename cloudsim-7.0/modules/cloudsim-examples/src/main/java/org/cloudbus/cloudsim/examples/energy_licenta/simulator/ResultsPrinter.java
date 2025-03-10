package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.cloudbus.cloudsim.examples.energy_licenta.utils.EnergyCalculator.calculateEnergyConsumption;

public class ResultsPrinter {

    public static void printResults(DatacenterBroker broker, List<Vm> vmList, SchedulingAlgorithm algorithm) {
        List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
        double totalEnergyConsumption = 0.0;

        // Header formatat corect
        System.out.printf("%-12s %-10s %-5s %-12s %-12s %-12s %-18s%n",
                "CloudletID", "Status", "VM", "Start Time", "Finish Time", "Exec Time", "Energy Consumption");
        System.out.println("-------------------------------------------------------------------------------");

        // Date formatate cu padding fix
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("CloudletID,Status,VM,Start Time,Finish Time,Exec Time,Energy Consumption\n");

        for (Cloudlet cloudlet : cloudletList) {
            Vm assignedVm = null;
            for (Vm vm : vmList) {
                if (vm.getId() == cloudlet.getVmId()) {
                    assignedVm = vm;
                    break;
                }
            }

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

            fileContent.append(String.format("%d,%s,%d,%.2f,%.2f,%.2f,%.2f\n",
                    cloudlet.getCloudletId(),
                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime(),
                    cloudlet.getActualCPUTime(),
                    energyConsumption));
        }

        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf("Algorithm Used: %s%n", algorithm.getClass().getSimpleName());
        System.out.printf("TOTAL ENERGY CONSUMPTION: %.2f kWh%n", totalEnergyConsumption);

        // Salvează rezultatele într-un fișier CSV
        writeResultsToFile("simulation_results.csv", fileContent.toString());
    }

    private static void writeResultsToFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            System.out.println("* Rezultatele au fost salvate în: " + fileName);
        } catch (IOException e) {
            System.err.println("! Eroare la scrierea în fișier: " + e.getMessage());
        }
    }

}
