package org.cloudbus.cloudsim.examples.energy_licenta.algorithms;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.List;

public interface SchedulingAlgorithm {
    void runAlgorithm(DatacenterBroker broker, List<Vm> vmList, List<Cloudlet> cloudletList);
}
