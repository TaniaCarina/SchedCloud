package org.cloudbus.cloudsim.examples.energy_licenta.resource_manager;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.*;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic.FCFS;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic.RandomScheduler;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic.RoundRobin;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.heuristic.*;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.ACO;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.Genetic;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.PSO;

import java.util.*;

import static org.cloudbus.cloudsim.examples.energy_licenta.scaling.VMConsolidation.*;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.CloudletManager.createCloudlets;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.DatacenterManager.*;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.ResultsPrinter.*;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.VMManager.createVMs;

public class EnergySimulatorNormal {

    public static String runSimulation(int numHosts, int hostMIPS, int hostRAM,
                                       int numVMs, int vmMIPS, int vmRAM, long vmBW, long vmSize, int pesNumber,
                                       int numCloudlets, String algorithmName) {
        try {
            // Inițializare CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // Creare Datacenter cu parametrii corecți
            Datacenter datacenter = createDatacenterNormal("Datacenter_0", numHosts, hostMIPS, hostRAM);

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
                    algorithm = new MinLengthRoundRobin();
            }

            // Creare VM-uri fara scalare dinamica
            List<Vm> vmList = createVMs(broker.getId(), numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber);

            fakeConsolidateVMs(vmList, cloudletList);
            broker.submitGuestList(vmList);


            // Rulare algoritm de scheduling
            algorithm.runAlgorithm(broker, vmList, cloudletList);


            CloudSim.startSimulation();

            return printResultsStringBuilder(broker,  vmList, algorithm);


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


