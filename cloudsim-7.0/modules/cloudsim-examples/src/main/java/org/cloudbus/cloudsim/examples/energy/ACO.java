package org.cloudbus.cloudsim.examples.energy;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.ArrayList;
import java.util.List;



public class ACO implements Algorithms.SchedulingAlgorithm {
    private final int numAnts = 10;
    private final double evaporationRate = 0.5;
    private final double pheromoneIncrease = 1.0;
    private final int iterations = 100;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmlist, List<Cloudlet> cloudletList) {
        this.vmList = vmlist;
        this.cloudletList = cloudletList;

        double[][] pheromoneMatrix = initializePheromoneMatrix();
        List<Ant> ants = new ArrayList<>();

        for (int iter = 0; iter < iterations; iter++) {
            ants = createAnts();
            for (Ant ant : ants) {
                Vm selectedVm = selectVm(ant, pheromoneMatrix);
                ant.setVm(selectedVm);
                updatePheromone(pheromoneMatrix, ant, selectedVm);
            }
            evaporatePheromones(pheromoneMatrix);
        }

        for (Ant ant : ants) {
            broker.bindCloudletToVm(ant.getCloudlet().getCloudletId(), ant.getVm().getId());
        }

        System.out.println("~~~ ACO Algorithm applied! ~~~");
    }

    private double[][] initializePheromoneMatrix() {
        double[][] pheromoneMatrix = new double[cloudletList.size()][vmList.size()];
        for (int i = 0; i < cloudletList.size(); i++) {
            for (int j = 0; j < vmList.size(); j++) {
                pheromoneMatrix[i][j] = 1.0; // Inițializare uniformă
            }
        }
        return pheromoneMatrix;
    }

    private List<Ant> createAnts() {
        List<Ant> ants = new ArrayList<>();
        for (Cloudlet cloudlet : cloudletList) {
            ants.add(new Ant(cloudlet));
        }
        return ants;
    }

    private Vm selectVm(Ant ant, double[][] pheromoneMatrix) {
        int randomIndex = (int) (Math.random() * vmList.size());
        return vmList.get(randomIndex);
    }

    private void updatePheromone(double[][] pheromoneMatrix, Ant ant, Vm selectedVm) {
        int cloudletIndex = cloudletList.indexOf(ant.getCloudlet());
        int vmIndex = vmList.indexOf(selectedVm);
        pheromoneMatrix[cloudletIndex][vmIndex] += pheromoneIncrease;
    }

    private void evaporatePheromones(double[][] pheromoneMatrix) {
        for (int i = 0; i < pheromoneMatrix.length; i++) {
            for (int j = 0; j < pheromoneMatrix[i].length; j++) {
                pheromoneMatrix[i][j] *= (1 - evaporationRate);
            }
        }
    }

    public static class Ant {
        private Cloudlet cloudlet;
        private Vm vm;

        public Ant(Cloudlet cloudlet) {
            this.cloudlet = cloudlet;
        }

        public Cloudlet getCloudlet() {
            return cloudlet;
        }

        public Vm getVm() {
            return vm;
        }

        public void setVm(Vm vm) {
            this.vm = vm;
        }
    }
}
