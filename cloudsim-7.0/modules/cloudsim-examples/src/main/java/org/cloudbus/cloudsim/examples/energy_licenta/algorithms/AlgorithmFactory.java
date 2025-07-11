package org.cloudbus.cloudsim.examples.energy_licenta.algorithms;

import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.basic.*;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.ACO;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.Genetic;
import org.cloudbus.cloudsim.examples.energy_licenta.algorithms.metaheuristic.PSO;

public class AlgorithmFactory {

    public static SchedulingAlgorithm getAlgorithm(String name) {
        return switch (name) {
            case "FCFS" -> new FCFS();
            case "RoundRobin" -> new RoundRobin();
            case "MinMin" -> new MinMin();
            case "MaxMin" -> new MaxMin();
            case "LJF" -> new LJF();
            case "MinLengthRoundRobin" -> new MinLengthRoundRobin();
            case "RandomScheduler" -> new RandomScheduler();
            case "PSO" -> new PSO();
            case "ACO" -> new ACO();
            case "Genetic" -> new Genetic();
            default -> throw new IllegalArgumentException("Unknown algorithm: " + name);
        };
    }
}
