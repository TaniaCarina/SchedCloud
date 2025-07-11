package org.cloudbus.cloudsim.examples.energy_licenta.resource_manager;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.*;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic.*;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.ACO;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.Genetic;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.PSO;

import java.util.Calendar;
import java.util.List;

import static org.cloudbus.cloudsim.examples.energy_licenta.scaling.VMConsolidation.consolidateVMs;
import static org.cloudbus.cloudsim.examples.energy_licenta.scaling.VMScaler.scaleUpVMs;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.CloudletManager.createCloudlets;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.DatacenterManager.createDatacenter;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.ResultsPrinter.printResultsStringBuilder;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.VMManager.createDynamicVMs;

public class EnergySimulatorDynamic {

    public static String runSimulation(int numHosts, int hostMIPS, int hostRAM,
                                       int numVMs, int vmMIPS, int vmRAM, long vmBW, long vmSize, int pesNumber,
                                       int numCloudlets, String algorithmName) {
        try {
            // Inițializare CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // Creare Datacenter
            Datacenter datacenter = createDatacenter("Datacenter_0", numHosts, hostMIPS, hostRAM);

            // Creare Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Creare Cloudlets
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), numCloudlets);
            broker.submitCloudletList(cloudletList);

            // Alegere algoritm de scheduling
            SchedulingAlgorithm algorithm;
            switch (algorithmName) {
                case "MinLengthRoundRobin":
                    algorithm = new MinLengthRoundRobin();
                    break;
                case "ACO":
                    algorithm = new ACO();
                    break;
                case "FCFS":
                    algorithm = new FCFS();
                    break;
                case "RoundRobin":
                    algorithm = new RoundRobin(); break;
                case "Random":
                    algorithm = new RandomScheduler(); break;
                case "LJF":
                    algorithm = new LJF(); break;
                case "MinMin":
                    algorithm = new MinMin(); break;
                case "MaxMin":
                    algorithm = new MaxMin(); break;
                case "PSO":
                    algorithm = new PSO(); break;
                case "Genetic":
                    algorithm = new Genetic(); break;
                default:
                    System.out.println("Invalid algorithm! Defaulting to RoundRobin.");
                    algorithm = new MinLengthRoundRobin();
            }

            // Creare VM-uri dinamice
            List<Vm> vmList = createDynamicVMs(broker.getId(), numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber);

            // Aplicare scalare dinamică
            scaleUpVMs(broker, vmList, numCloudlets / 3); // Scalare dacă este necesar
            consolidateVMs(vmList, cloudletList); // Consolidare VM-uri
            //shutdownIdleVMs(vmList, cloudletList); // Oprire VM-uri neutilizate

            broker.submitGuestList(vmList);

            // Rulare algoritm de scheduling
            algorithm.runAlgorithm(broker, vmList, cloudletList);

            // Start simulare
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Returnam rezultatele ca un String
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
//
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
//            Datacenter datacenter = createDatacenter("Datacenter_0", numHosts);
//
//            // Create Broker
//            DatacenterBroker broker = new DatacenterBroker("Broker");
//
//            // Create Cloudlets
//            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), numCloudlets);
//            broker.submitCloudletList(cloudletList);
//
//
//            //////////////// cu SCALARE DINAMICA
//            // Cream doar VM-urile necesare initial
//            List<Vm> vmList = createDynamicVMs(broker.getId(), numCloudlets);
//            broker.submitGuestList(vmList);
//
//            algorithm.runAlgorithm(broker, vmList, cloudletList);
//
//            // Apelam scaling-ul dupa scheduling
//            //scaleUpVMs(broker, vmList, 20); // Scalam daca e nevoie
//
//            scaleUpVMs(broker, vmList, numCloudlets / 3);
//
//            consolidateVMs(vmList, cloudletList); // Consolidare VM-uri
//
//            shutdownIdleVMs(vmList, cloudletList); // Oprim VM-urile neutilizate
//
//
//            // Start Simulation2
//            CloudSim.startSimulation();
//            CloudSim.stopSimulation();
//
//            // Print Results
//            printResults(broker, vmList, algorithm);
//
//            //printResults(broker);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }




