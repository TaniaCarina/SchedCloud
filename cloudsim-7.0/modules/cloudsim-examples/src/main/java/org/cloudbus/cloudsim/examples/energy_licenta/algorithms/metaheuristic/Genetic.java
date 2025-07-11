package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.*;

/**
 * Algoritmul simuleaza o populatie de solutii posibile (cromozomi), fiecare reprezentand o alocare de cloudlet-uri pe VM-uri
 * Fiecare cromozom este evaluat folosind o functie de fitness (timpul total de executie maxim per VM)
 * In fiecare generatie, cei mai buni indivizi sunt selectati pentru reproducere
 * Se aplica incrucisare (crossover) intre doua solutii pentru a genera una noua
 * Cu o anumita probabilitate, se aplica mutatie (modificare aleatorie a unei gene)
 * Dupa un numar de generatii, se alege cea mai buna solutie si se face alocarea reala
 */

public class Genetic implements SchedulingAlgorithm {

    private static final int POPULATION_SIZE = 30;
    private static final int GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.8;

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList) {
        int numCloudlets = cloudletList.size();
        int numVMs = vmList.size();
        Random random = new Random();

        List<int[]> population = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            int[] chromosome = new int[numCloudlets];
            for (int j = 0; j < numCloudlets; j++) {
                chromosome[j] = random.nextInt(numVMs);
            }
            population.add(chromosome);
        }

        for (int generation = 0; generation < GENERATIONS; generation++) {
            population.sort(Comparator.comparingDouble(individual -> fitness(individual, cloudletList, vmList)));

            List<int[]> newPopulation = new ArrayList<>();
            while (newPopulation.size() < POPULATION_SIZE) {
                int[] parent1 = population.get(random.nextInt(POPULATION_SIZE / 2));
                int[] parent2 = population.get(random.nextInt(POPULATION_SIZE / 2));

                int[] child;
                if (random.nextDouble() < CROSSOVER_RATE) {
                    child = crossover(parent1, parent2);
                } else {
                    child = parent1.clone();
                }

                if (random.nextDouble() < MUTATION_RATE) {
                    mutate(child, numVMs);
                }

                newPopulation.add(child);
            }
            population = newPopulation;
        }

        int[] best = population.get(0);
        for (int i = 0; i < numCloudlets; i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get(best[i]);
            cl.setVmId(vm.getId());
            broker.bindCloudletToVm(cl.getCloudletId(), vm.getId());
        }

        System.out.println("Genetic Algorithm applied!");
    }

    private double fitness(int[] chromosome, List<Cloudlet> cloudletList, List<Vm> vmList) {
        Map<Integer, Double> vmLoads = new HashMap<>();
        for (Vm vm : vmList) vmLoads.put(vm.getId(), 0.0);

        for (int i = 0; i < chromosome.length; i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get(chromosome[i]);
            double exec = cl.getCloudletLength() / (double) vm.getMips();
            vmLoads.put(vm.getId(), vmLoads.get(vm.getId()) + exec);
        }
        return Collections.max(vmLoads.values());
    }

    private int[] crossover(int[] p1, int[] p2) {
        int point = new Random().nextInt(p1.length);
        int[] child = new int[p1.length];
        System.arraycopy(p1, 0, child, 0, point);
        System.arraycopy(p2, point, child, point, p1.length - point);
        return child;
    }

    private void mutate(int[] chromosome, int numVMs) {
        int idx = new Random().nextInt(chromosome.length);
        chromosome[idx] = new Random().nextInt(numVMs);
    }
}