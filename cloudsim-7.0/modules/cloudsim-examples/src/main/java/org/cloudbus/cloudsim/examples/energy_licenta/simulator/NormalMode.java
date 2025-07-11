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
            // Initializare CloudSim
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

            // Trimite VM-uri si Cloudlet-uri la broker
            broker.submitGuestList(vmList);
            broker.submitCloudletList(cloudletList);

            // Asigura corect HostId in VM-uri
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

