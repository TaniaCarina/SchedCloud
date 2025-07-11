package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.*;

import java.util.Calendar;
import java.util.List;

import static org.cloudbus.cloudsim.examples.energy_licenta.scaling.VMConsolidation.consolidateVMs;
import static org.cloudbus.cloudsim.examples.energy_licenta.scaling.VMScaler.scaleUpVMs;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.CloudletManager.createCloudlets;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.DatacenterManager.createDatacenter;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.ResultsPrinter.printResultsStringBuilder;
import static org.cloudbus.cloudsim.examples.energy_licenta.resource_manager.VMManager.createDynamicVMs;

public class EnergyEfficientMode {

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
            SchedulingAlgorithm algorithm = AlgorithmFactory.getAlgorithm(algorithmName);

            // Creare VM-uri dinamice
            List<Vm> vmList = createDynamicVMs(broker.getId(), numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber);

            // Aplicare scalare dinamica
            scaleUpVMs(broker, vmList, numCloudlets / 3); // Scalare daca este necesar
            consolidateVMs(vmList, cloudletList); // Consolidare VM-uri

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



