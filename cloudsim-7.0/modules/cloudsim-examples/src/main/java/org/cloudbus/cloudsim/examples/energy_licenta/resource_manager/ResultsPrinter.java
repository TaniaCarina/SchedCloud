package org.cloudbus.cloudsim.examples.energy_licenta.resource_manager;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.cloudbus.cloudsim.examples.energy_licenta.utils.EnergyCalculator.calculateEnergyConsumption;

public class ResultsPrinter {

    private static final Map<Integer, Integer> vmToHostMap = new HashMap<>(); //  Salveaza VM-Host

    public static String printResultsStringBuilder(DatacenterBroker broker, List<Vm> vmList, SchedulingAlgorithm algorithm) {
        List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
        double totalEnergyConsumption = 0.0;

        // StringBuilder pentru rezultate
        StringBuilder resultBuilder = new StringBuilder();

        // Header formatat corect (inclusiv Host)
        resultBuilder.append(String.format("%-12s %-10s %-5s %-5s %-12s %-12s %-12s %-18s%n",
                "CloudletID", "Status", "VM", "Host", "Start Time", "Finish Time", "Exec Time", "Energy Consumption"));
        resultBuilder.append("--------------------------------------------------------------------------------------\n");

        // CSV Header
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("CloudletID,Status,VM,Host,Start Time,Finish Time,Exec Time,Energy Consumption\n");

        for (Cloudlet cloudlet : cloudletList) {
            Vm assignedVm = null;
            int hostId = -1;

            // Gasim VM-ul și Host-ul asociat cloudlet-ului
            for (Vm vm : vmList) {
                if (vm.getId() == cloudlet.getVmId()) {
                    assignedVm = vm;
                    Host host = (Host) vm.getHost();
                    // Verificam daca VM-ul are un Host alocat
                    if (host != null) {
                        hostId = vm.getHost().getId();
                    } else {
                        hostId = -1;  // VM-ul nu are host alocat
                    }
                    break;
                }
            }

            double energyConsumption = (assignedVm != null) ? calculateEnergyConsumption(cloudlet, assignedVm) : 0.0;
            totalEnergyConsumption += energyConsumption;

            // Adaugare în StringBuilder pentru UI
            resultBuilder.append(String.format("%-12d %-10s %-5d %-5d %-12.2f %-12.2f %-12.2f %-18.2f%n",
                    cloudlet.getCloudletId(),
                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    hostId,
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime(),
                    cloudlet.getActualCPUTime(),
                    energyConsumption));

            // Adaugare în CSV
            fileContent.append(String.format("%d,%s,%d,%d,%.2f,%.2f,%.2f,%.2f\n",
                    cloudlet.getCloudletId(),
                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    hostId,
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime(),
                    cloudlet.getActualCPUTime(),
                    energyConsumption));
        }

        resultBuilder.append("--------------------------------------------------------------------------------------\n");
        resultBuilder.append(String.format("Algorithm Used: %s%n", algorithm.getClass().getSimpleName()));
        resultBuilder.append(String.format("TOTAL ENERGY CONSUMPTION: %.2f kWh%n", totalEnergyConsumption));

        // Salveaza rezultatele într-un fișier CSV
        writeResultsToFile("simulation_results.csv", fileContent.toString());

        // Returneaza rezultatele pentru UI
        return resultBuilder.toString();
    }



    public static void printResults(DatacenterBroker broker, List<Vm> vmList, SchedulingAlgorithm algorithm) {
        List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
        double totalEnergyConsumption = 0.0;

        // Header formatat corect (inclusiv Host)
        System.out.printf("%-12s %-10s %-5s %-5s %-12s %-12s %-12s %-18s%n",
                "CloudletID", "Status", "VM", "Host", "Start Time", "Finish Time", "Exec Time", "Energy Consumption");
        System.out.println("--------------------------------------------------------------------------------------");

        // Date formatate cu padding fix
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("CloudletID,Status,VM,Host,Start Time,Finish Time,Exec Time,Energy Consumption\n");

        for (Cloudlet cloudlet : cloudletList) {
            Vm assignedVm = null;
            int hostId = -1;

            // Gasim VM-ul si Host-ul asociat cloudlet-ului
            for (Vm vm : vmList) {
                if (vm.getId() == cloudlet.getVmId()) {
                    assignedVm = vm;

                    // Verificam daca VM-ul are un Host alocat
                    if (vm.getHost() != null) {
                        hostId = vm.getHost().getId();
                    } else {
                        hostId = -1;  // VM-ul nu are host alocat
                    }
                    break;
                }
            }


            double energyConsumption = (assignedVm != null) ? calculateEnergyConsumption(cloudlet, assignedVm) : 0.0;
            totalEnergyConsumption += energyConsumption;

            // Afișare în consola
            System.out.printf("%-12d %-10s %-5d %-5d %-12.2f %-12.2f %-12.2f %-18.2f%n",
                    cloudlet.getCloudletId(),
                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    hostId,
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime(),
                    cloudlet.getActualCPUTime(),
                    energyConsumption);

            // Scriere în fișier CSV
            fileContent.append(String.format("%d,%s,%d,%d,%.2f,%.2f,%.2f,%.2f\n",
                    cloudlet.getCloudletId(),
                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    hostId,
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime(),
                    cloudlet.getActualCPUTime(),
                    energyConsumption));
        }

        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("Algorithm Used: %s%n", algorithm.getClass().getSimpleName());
        System.out.printf("TOTAL ENERGY CONSUMPTION: %.2f kWh%n", totalEnergyConsumption);

        // Salveaza rezultatele într-un fișier CSV
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



