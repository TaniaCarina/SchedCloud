package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.*;

/**
 * Ant Colony Optimization
 *
 * ACO este un algoritm metaeuristic inspirat din comportamentul furnicilor reale in cautarea hranei. In contextul planificarii cloudletelor,
 * ACO simuleaza mai multe "furnici" virtuale care exploreaza diferite moduri de a aloca task-urile pe masini virtuale (VM-uri).
 * Fiecare furnica construieste o solutie posibila, iar pe baza performantei acesteia, se actualizeaza o matrice de feromoni care ghideaza alegerile viitoare.
 * Feromonii sunt consolidati acolo unde solutiile sunt bune si se evapora in timp, evitand astfel blocajul intr-o singura solutie.
 * Scopul algoritmului este sa gaseasca o alocare cat mai eficienta a task-urilor pentru a minimiza timpul de executie sau consumul de energie.
 */

public class ACO implements SchedulingAlgorithm {
    private final double evaporationRate = 0.5;
    private final double pheromoneIncrease = 1.0;
    private final int iterations = 100;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private Map<String, Double> pheromoneMap;

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmlist, List<Cloudlet> cloudletList) {
        this.vmList = vmlist;
        this.cloudletList = cloudletList;

        initializePheromoneMap();
        List<Ant> ants = new ArrayList<>();

        for (int it = 0; it< iterations; it++) {
            ants = createAnts();
            for (Ant ant : ants) {
                Vm selectedVm = selectVm(ant);
                ant.setVm(selectedVm);
                updatePheromone(ant, selectedVm);
            }
            evaporatePheromones();
        }

        for (Ant ant : ants) {
            broker.bindCloudletToVm(ant.getCloudlet().getCloudletId(), ant.getVm().getId());
        }

        System.out.println("ACO Algorithm applied!");
    }

    private void initializePheromoneMap() {
        pheromoneMap = new HashMap<>();
        for (Cloudlet cloudlet : cloudletList) {
            for (Vm vm : vmList) {
                String key = generateKey(cloudlet.getCloudletId(), vm.getId());
                pheromoneMap.put(key, 1.0);
            }
        }
    }

    private List<Ant> createAnts() {
        List<Ant> ants = new ArrayList<>();
        for (Cloudlet cloudlet : cloudletList) {
            ants.add(new Ant(cloudlet));
        }
        return ants;
    }

    private Vm selectVm(Ant ant) {
        // Random VM selection
        int randomIndex = (int) (Math.random() * vmList.size());
        return vmList.get(randomIndex);
    }

    private void updatePheromone(Ant ant, Vm selectedVm) {
        String key = generateKey(ant.getCloudlet().getCloudletId(), selectedVm.getId());
        double performance = evaluateSolution(ant, selectedVm);
        double delta = pheromoneIncrease / performance; // mai bun = mai mult feromon
        pheromoneMap.put(key, pheromoneMap.getOrDefault(key, 1.0) + delta);
    }


    private void evaporatePheromones() {
        for (String key : pheromoneMap.keySet()) {
            pheromoneMap.put(key, pheromoneMap.get(key) * (1 - evaporationRate));
        }
    }

    private double evaluateSolution(Ant ant, Vm vm) {
        //estimare timp de execuție
        long length = ant.getCloudlet().getCloudletLength();
        long mips = (long) vm.getMips();
        int pes = vm.getNumberOfPes();
        return (double) length / (mips * pes);
    }

    private String generateKey(int cloudletId, int vmId) {
        return cloudletId + ":" + vmId;
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
