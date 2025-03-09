package org.cloudbus.cloudsim.examples.energy;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EnergyManagementSimulator {
    public static void main(String[] args) {
        int numHosts = 10;
        int numVMs = 20;
        int numCloudlets = 50;

        try {
            // Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // Create Datacenter
            Datacenter datacenter = createDatacenter("Datacenter_0", numHosts);

            // Create Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");

//            // Create VMs
//            List<Vm> vmList = createVMs(broker.getId(), numVMs);
//            broker.submitGuestList(vmList);


            // Create Cloudlets
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), numCloudlets);
            broker.submitCloudletList(cloudletList);

//            // Apply Energy-Aware Min-Min Scheduling
//            EnergyAwareMinMinScheduler.schedule(vmList, cloudletList); // Scheduling
//            consolidateVMs(vmList, cloudletList); // Consolidare VM-uri
//            shutdownIdleVMs(vmList, cloudletList); // Oprire VM-uri idle



            //////////////// cu SCALARE DINAMICA
            // Creăm doar VM-urile necesare inițial
            List<Vm> vmList = createDynamicVMs(broker.getId(), numCloudlets);
            broker.submitGuestList(vmList);

            // Apelăm scaling-ul după scheduling
            EnergyAwareMinMinScheduler.schedule(vmList, cloudletList);
            scaleUpVMs(broker, vmList, 100); // Scalăm dacă e nevoie
            shutdownIdleVMs(vmList, cloudletList); // Oprim VM-urile neutilizate



            // Start Simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Print Results
            printResults(broker, vmList);
            //printResults(broker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////////// SCALARE DINAMICA LA VM-URI ///////////////////////

    // nu e deloc eficienta, energie consumata aproape dubla
    private static List<Vm> createDynamicVMs(int brokerId, int numCloudlets) {
        List<Vm> vmList = new ArrayList<>();
        int initialVMs = Math.max(50, numCloudlets /50); // Începem cu un minim de 5 VM-uri sau 1 VM la fiecare 5 cloudlet-uri

        for (int i = 0; i < initialVMs; i++) {
            int mips = 500;
            int ram = 2048;
            long bw = 1000;
            long size = 10000;
            Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        System.out.println("✅ [Scaling] Pornite initial " + initialVMs + " VM-uri.");
        return vmList;
    }

    ////////////////////////////////////////////////////////////////////////


    private static void scaleUpVMs(DatacenterBroker broker, List<Vm> vmList, int maxVMs) {
        int activeVMs = vmList.size();
        int waitingCloudlets = (int) broker.getCloudletSubmittedList().stream()
                .filter(cloudlet -> !cloudlet.isFinished())
                .count();



        if (waitingCloudlets > activeVMs * 2 && activeVMs < maxVMs) {
            int newVMs = Math.min(5, maxVMs - activeVMs); // Adăugăm maxim 5 VM-uri odată

            for (int i = 0; i < newVMs; i++) {
                int newId = activeVMs + i;
                Vm vm = new Vm(newId, broker.getId(), 500, 1, 2048, 1000, 10000, "Xen", new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }
            System.out.println("✅ [Scaling] Adăugate " + newVMs + " VM-uri suplimentare.");
        }
    }


    private static Datacenter createDatacenter(String name, int numHosts) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new Pe(0, new PeProvisionerSimple(1000))); // 1 CPU core with 1000 MIPS

            int ram = 20480;
            long storage = 1000000;
            int bw = 10000;

            Host host = new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList));
            hostList.add(host);
        }
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = 0.01;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.01;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, timeZone, costPerSec, costPerMem, costPerStorage, costPerBw);
        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new ArrayList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Vm> createVMs(int brokerId, int numVMs) {
        List<Vm> vmList = new ArrayList<>();
        for (int i = 0; i < numVMs; i++) {
            int mips = 500;
            int ram = 2048;
            long bw = 1000;
            long size = 10000;
            Vm vm = new Vm(i, brokerId, mips, 1, ram, bw, size, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        return vmList;
    }

    private static List<Cloudlet> createCloudlets(int brokerId, int numCloudlets) {
        List<Cloudlet> cloudletList = new ArrayList<>();
        for (int i = 0; i < numCloudlets; i++) {
            int length = (int) (Math.random() * 5000) + 1000;
            int pesNumber = 1;
            long fileSize = 300;
            long outputSize = 300;
            Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }

    //================ INITIAL ======================
//    private static double calculateEnergyConsumption(Cloudlet cloudlet) {
//        double executionTime = cloudlet.getActualCPUTime();
//        double mips = 500; // Presupunem că fiecare VM are 500 MIPS
//
//        // Formula de consum energetic: Puterea (în Watt) * Timpul de execuție
//        // Aproximăm puterea consumată de un VM la 0.5W per MIPS utilizat
//        double powerPerMIPS = 0.5;
//        return executionTime * mips * powerPerMIPS / 1000.0; // Convertim la kWh
//    }



    // ======================= Dynamic Voltage and Frequency Scaling (DVFS)
    // ======================= DVFS permite ajustarea frecventei CPU pentru a reduce consumul de energie atunci cand VM-ul nu este folosit intens.
    private static double calculateEnergyConsumption(Cloudlet cloudlet, Vm vm) {
        double executionTime = cloudlet.getActualCPUTime();

        // DVFS: Dacă VM-ul are o utilizare CPU sub 50%, reducem frecvența
        double frequencyFactor = vm.getMips() > 250 ? 1.0 : 0.7;  // Dacă MIPS < 250, frecvența e mai mică

        double powerPerMIPS = 0.5 * frequencyFactor;  // Reducem consumul energetic
        return executionTime * vm.getMips() * powerPerMIPS / 1000.0;  // Convertim la kWh
    }



    // ===================== Oprirea VM-urilor Idle pentru economisirea energiei
    // ===================== Daca un VM nu are cloudlet-uri alocate, îl oprim pentru a reduce consumul.
    private static void shutdownIdleVMs(List<Vm> vmList, List<Cloudlet> cloudletList) {
        boolean vmShutDown = false;
        for (Vm vm : vmList) {
            boolean isUsed = false;
            for (Cloudlet cloudlet : cloudletList) {
                if (cloudlet.getVmId() == vm.getId()) {
                    isUsed = true;
                    break;
                }
            }

            if (!isUsed) {
                System.out.println("~ [Energy Optimization - shutdownIdleVMs] Oprire VM #" + vm.getId() + " pentru a economisi energie.");
                vm.setMips(0); // Simulăm oprirea VM-ului
                vmShutDown = true;
            }
        }

        if (!vmShutDown) {
            System.out.println(" ~ [Energy Optimization - shutdownIdleVMs] Toate VM-urile au fost utilizate. Niciun VM nu a fost oprit.");
        }
    }



    // =================== Consolidarea VM-urilor pentru reducerea consumului
    // =================== in loc sa folosim toate VM-urile de la inceput, incepem doar cu cate VM-uri sunt necesare si sa alocam mai multe doar daca e nevoie.
    private static void consolidateVMs(List<Vm> vmList, List<Cloudlet> cloudletList) {
        int activeVMs = Math.min(vmList.size(), (cloudletList.size() / 3) + 1); // Folosim doar VM-urile necesare

        for (int i = activeVMs; i < vmList.size(); i++) {
            vmList.get(i).setMips(0); // Oprire VM-uri neutilizate
            System.out.println(" ~!!!~ Consolidare: Oprire VM #" + vmList.get(i).getId());
        }
    }


    // =============== INITIAL fara optimizarea consumului de energie ================
//    private static void printResults(DatacenterBroker broker) {
//        List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
//
//        // Header formatat corect
//        System.out.printf("%-12s %-10s %-5s %-12s %-18s%n",
//                "CloudletID", "Status", "VM", "Exec Time", "Energy Consumption");
//        System.out.println("--------------------------------------------------------------");
//
//        // Date formatate cu padding fix
//        for (Cloudlet cloudlet : cloudletList) {
//            double energyConsumption = calculateEnergyConsumption(cloudlet);
//            System.out.printf("%-12d %-10s %-5d %-12.2f %-18.2f%n",
//                    cloudlet.getCloudletId(),
//                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
//                    cloudlet.getVmId(),
//                    cloudlet.getActualCPUTime(),
//                    energyConsumption);
//        }
//    }

    private static void printResults(DatacenterBroker broker, List<Vm> vmList) {
        List<Cloudlet> cloudletList = broker.getCloudletReceivedList();

        // Header formatat corect
        System.out.printf("%-12s %-10s %-5s %-12s %-18s%n",
                "CloudletID", "Status", "VM", "Exec Time", "Energy Consumption");
        System.out.println("--------------------------------------------------------------");

        // Date formatate cu padding fix
        for (Cloudlet cloudlet : cloudletList) {
            // Găsim VM-ul asociat cloudlet-ului
            Vm assignedVm = null;
            for (Vm vm : vmList) {
                if (vm.getId() == cloudlet.getVmId()) {
                    assignedVm = vm;
                    break;
                }
            }

            // Calculăm consumul de energie doar dacă găsim VM-ul asociat
            double energyConsumption = (assignedVm != null) ?
                    calculateEnergyConsumption(cloudlet, assignedVm) : 0.0;

            System.out.printf("%-12d %-10s %-5d %-12.2f %-18.2f%n",
                    cloudlet.getCloudletId(),
                    cloudlet.isFinished() ? "SUCCESS" : "FAILED",
                    cloudlet.getVmId(),
                    cloudlet.getActualCPUTime(),
                    energyConsumption);
        }
    }


}
