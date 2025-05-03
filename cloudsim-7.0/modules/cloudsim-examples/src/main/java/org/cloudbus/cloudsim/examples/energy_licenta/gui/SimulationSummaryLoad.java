package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import java.sql.Timestamp;

public class SimulationSummaryLoad {
    private int id;
    private String algorithm;
    private boolean dynamicScaling;
    private int hosts;
    private int vms;
    private int cloudlets;
    private double totalEnergy;
    private double realExecTime;
    private double cloudletExecTime;
    private Timestamp simTimestamp;

    public SimulationSummaryLoad(int id, String algorithm, boolean dynamicScaling,
                                 int hosts, int vms, int cloudlets,
                                 double totalEnergy, double realExecTime, double cloudletExecTime,
                                 Timestamp simTimestamp) {
        this.id = id;
        this.algorithm = algorithm;
        this.dynamicScaling = dynamicScaling;
        this.hosts = hosts;
        this.vms = vms;
        this.cloudlets = cloudlets;
        this.totalEnergy = totalEnergy;
        this.realExecTime = realExecTime;
        this.cloudletExecTime = cloudletExecTime;
        this.simTimestamp = simTimestamp;
    }

    public int getId() { return id; }
    public String getAlgorithm() { return algorithm; }
    public boolean isDynamicScaling() { return dynamicScaling; }
    public int getHosts() { return hosts; }
    public int getVms() { return vms; }
    public int getCloudlets() { return cloudlets; }
    public double getTotalEnergy() { return totalEnergy; }
    public double getRealExecTime() { return realExecTime; }
    public double getCloudletExecTime() { return cloudletExecTime; }
    public Timestamp getSimTimestamp() { return simTimestamp; }
}
