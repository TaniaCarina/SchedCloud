package org.cloudbus.cloudsim.examples.energy_licenta.db;

public class SimulationResult {
    private String cloudletId;
    private String status;
    private String vmId;
    private String hostId;
    private double startTime;
    private double finishTime;
    private double execTime;
    private double energy;

    public SimulationResult(String cloudletId, String status, String vmId, String hostId,
                            double startTime, double finishTime, double execTime, double energy) {
        this.cloudletId = cloudletId;
        this.status = status;
        this.vmId = vmId;
        this.hostId = hostId;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.execTime = execTime;
        this.energy = energy;
    }

    public String getCloudletId() {
        return cloudletId;
    }

    public String getStatus() {
        return status;
    }

    public String getVmId() {
        return vmId;
    }

    public String getHostId() {
        return hostId;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public double getExecTime() {
        return execTime;
    }

    public double getEnergy() {
        return energy;
    }
}
