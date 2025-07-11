package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.*;
import org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.DatacenterManager;

import java.util.*;

import static org.cloudbus.cloudsim.examples.energy_licenta.scaling.VMConsolidation.*;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.CloudletManager.createCloudlets;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.ResultsPrinter.*;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.VMManager.createVMs;

public class NormalMode {

    public static String runSimulation(int numHosts, int hostMIPS, int hostRAM,
                                       int numVMs, int vmMIPS, int vmRAM, long vmBW, long vmSize, int pesNumber,
                                       int numCloudlets, String algorithmName) {
        try {
            // Inițializare CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            Datacenter datacenter0 = DatacenterManager.createDatacenterNormal("Datacenter_0", numHosts, hostMIPS, hostRAM);

            // Creare Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Creare Cloudlets
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), numCloudlets);
            broker.submitCloudletList(cloudletList);

            // Creare VM-uri
            List<Vm> vmList = createVMs(broker.getId(), numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber);

            // Alegere algoritm de scheduling
            SchedulingAlgorithm algorithm = AlgorithmFactory.getAlgorithm(algorithmName);

            fakeConsolidateVMs(vmList, cloudletList);

            // Trimite VM-uri și Cloudlet-uri la broker
            broker.submitGuestList(vmList);
            broker.submitCloudletList(cloudletList);

            // Asigura corect HostId în VM-uri
            DatacenterManager.bindVMsToHosts(vmList, datacenter0);

            // Rulare algoritm
            algorithm.runAlgorithm(broker, vmList, cloudletList);

            // Pornire simulare
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Returnare rezultate
            return printResultsStringBuilder(broker, vmList, algorithm);


        } catch (Exception e) {
            e.printStackTrace();
            return "Simulation failed due to an error!";
        }
    }
}






//    public static void main(String[] args) {
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Select Scheduling Algorithm:");
//        System.out.println("1. RoundRobin");
//        System.out.println("2. ACO (Ant Colony Optimization)");
//        System.out.println("3. FCFS (First-Come-First-Serve)");
//        int choice = scanner.nextInt();
//
//        SchedulingAlgorithm algorithm;
//        switch (choice) {
//            case 1:
//                algorithm = new RoundRobin();
//                break;
//            case 2:
//                algorithm = new ACO();
//                break;
//            case 3:
//                algorithm = new FCFS();
//                break;
//            default:
//                System.out.println("Invalid choice! Defaulting to RoundRobin.");
//                algorithm = new RoundRobin();
//        }
//
//        int numHosts = 10;
//        int numVMs = 40;
//        int numCloudlets = 50;
//
//        try {
//            // Initialize CloudSim
//            int numUsers = 1;
//            Calendar calendar = Calendar.getInstance();
//            CloudSim.init(numUsers, calendar, false);
//
//            // Create Datacenter
//            Datacenter datacenter = createDatacenter_normal("Datacenter_0", numHosts);
//            datacenterList.add(datacenter);
//
//            // Create Broker
//            DatacenterBroker broker = new DatacenterBroker("Broker");
//
//            // Create VMs FARA scalare dinamica
//            List<Vm> vmList = createVMs(broker.getId(), numVMs);
//            broker.submitGuestList(vmList);
//
//            // Create Cloudlets
//            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), numCloudlets);
//            broker.submitCloudletList(cloudletList);
//
//            // Execută algoritmul de scheduling
//            algorithm.runAlgorithm(broker, vmList, cloudletList);
//
//            // Start Simulation
//            CloudSim.startSimulation();
//            CloudSim.stopSimulation();
//
//            // Print Results
//            printResultsNormal(broker, vmList, algorithm);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


