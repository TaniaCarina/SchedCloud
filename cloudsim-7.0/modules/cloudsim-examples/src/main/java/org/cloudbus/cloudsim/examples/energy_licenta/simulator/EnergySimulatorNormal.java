package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.ACO;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.FCFS;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.RoundRobin;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import static org.cloudbus.cloudsim.examples.energy_licenta.simulator.CloudletManager.createCloudlets;
import static org.cloudbus.cloudsim.examples.energy_licenta.simulator.DatacenterManager.createDatacenter;
import static org.cloudbus.cloudsim.examples.energy_licenta.simulator.DatacenterManager.createDatacenter_normal;
import static org.cloudbus.cloudsim.examples.energy_licenta.simulator.ResultsPrinter.printResults;
//import static org.cloudbus.cloudsim.examples.energy_licenta.simulator.ResultsPrinter.printResultsNormal;
import static org.cloudbus.cloudsim.examples.energy_licenta.simulator.ResultsPrinter.printResultsNormal;
import static org.cloudbus.cloudsim.examples.energy_licenta.simulator.VMManager.createVMs;

public class EnergySimulatorNormal {

    public static List<Datacenter> datacenterList = new ArrayList<>(); // 🔹 Stocăm Datacenters

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select Scheduling Algorithm:");
        System.out.println("1. RoundRobin");
        System.out.println("2. ACO (Ant Colony Optimization)");
        System.out.println("3. FCFS (First-Come-First-Serve)");
        int choice = scanner.nextInt();

        SchedulingAlgorithm algorithm;
        switch (choice) {
            case 1:
                algorithm = new RoundRobin();
                break;
            case 2:
                algorithm = new ACO();
                break;
            case 3:
                algorithm = new FCFS();
                break;
            default:
                System.out.println("Invalid choice! Defaulting to RoundRobin.");
                algorithm = new RoundRobin();
        }

        int numHosts = 10;
        int numVMs = 40;
        int numCloudlets = 50;

        try {
            // Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // Create Datacenter
            Datacenter datacenter = createDatacenter_normal("Datacenter_0", numHosts);
            datacenterList.add(datacenter);

            // Create Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Create VMs FARA scalare dinamica
            List<Vm> vmList = createVMs(broker.getId(), numVMs);
            broker.submitGuestList(vmList);

            // Create Cloudlets
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), numCloudlets);
            broker.submitCloudletList(cloudletList);

            // Execută algoritmul de scheduling
            algorithm.runAlgorithm(broker, vmList, cloudletList);

            // Start Simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Print Results
            printResultsNormal(broker, vmList, algorithm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
