package org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.SchedulingAlgorithm;

import java.util.*;

/**
 * Particle Swarm Optimization
 *
 * Algoritmul simuleaza particule, fiecare reprezentand o posibila alocare a cloudletelor pe VM-uri
 * Fiecare particula are o pozitie (o combinatie VM pentru fiecare cloudlet) si o valoare de fitness (timpul maxim de executie pe un VM)
 * In fiecare iteratie, particulele isi actualizeaza pozitia printr-o forma simplificata de mutatie aleatoare
 * Se pastreaza cea mai buna solutie globala gasita (pozitia cu cel mai mic timp de executie total)
 * La final, cloudletele sunt alocate conform pozitiei celei mai bune particule
 */

public class PSO implements SchedulingAlgorithm {

    private static final int PARTICLE_COUNT = 30;
    private static final int MAX_ITERATIONS = 50;

    @Override
    public void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList) {
        int numCloudlets = cloudletList.size();
        int numVMs = vmList.size();

        List<Particle> swarm = new ArrayList<>();
        Random random = new Random();

        // Initialize particles
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            int[] position = new int[numCloudlets];
            for (int j = 0; j < numCloudlets; j++) {
                position[j] = random.nextInt(numVMs);
            }
            swarm.add(new Particle(position));
        }

        Particle globalBest = null;

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            for (Particle particle : swarm) {
                double fitness = evaluate(particle, cloudletList, vmList);
                if (fitness < particle.bestFitness) {
                    particle.bestFitness = fitness;
                    particle.bestPosition = particle.position.clone();
                }

                if (globalBest == null || fitness < globalBest.bestFitness) {
                    globalBest = new Particle(particle.position.clone());
                    globalBest.bestFitness = fitness;
                }
            }

            // Update particles (simplified: random mutation)
            for (Particle particle : swarm) {
                for (int j = 0; j < numCloudlets; j++) {
                    if (random.nextDouble() < 0.2) {
                        particle.position[j] = random.nextInt(numVMs);
                    }
                }
            }
        }

        // Final binding based on global best
        for (int i = 0; i < numCloudlets; i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get(globalBest.bestPosition[i]);
            cl.setVmId(vm.getId());
            broker.bindCloudletToVm(cl.getCloudletId(), vm.getId());
        }

        System.out.println("~~~ Particle Swarm Optimization (PSO) Scheduler applied! ~~~");
    }

    private double evaluate(Particle particle, List<Cloudlet> cloudletList, List<Vm> vmList) {
        Map<Integer, Double> vmTimes = new HashMap<>();
        for (Vm vm : vmList) {
            vmTimes.put(vm.getId(), 0.0);
        }
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cl = cloudletList.get(i);
            Vm vm = vmList.get(particle.position[i]);
            double execTime = cl.getCloudletLength() / (double) vm.getMips();
            vmTimes.put(vm.getId(), vmTimes.get(vm.getId()) + execTime);
        }
        return Collections.max(vmTimes.values());
    }

    static class Particle {
        int[] position;
        int[] bestPosition;
        double bestFitness;

        Particle(int[] position) {
            this.position = position;
            this.bestPosition = position.clone();
            this.bestFitness = Double.MAX_VALUE;
        }
    }
}