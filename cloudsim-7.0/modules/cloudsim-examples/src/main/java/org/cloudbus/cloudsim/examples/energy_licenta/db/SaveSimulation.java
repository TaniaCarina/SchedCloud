package org.cloudbus.cloudsim.examples.energy_licenta.db;

import org.cloudbus.cloudsim.examples.energy_licenta.gui.ResultsTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SaveSimulation {
    public static void saveSummary(
            String simulationId,
            String algorithm, boolean isDynamic,
            int hosts, int vms, int cloudlets,
            double totalEnergy, double realExecTime, double cloudletExecTime
    ) {
        String sql = """
        INSERT INTO simulation_summary 
        (simulation_id, algorithm, dynamic_scaling, hosts, vms, cloudlets, total_energy, real_exec_time, cloudlet_exec_time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, simulationId);
            stmt.setString(2, algorithm);
            stmt.setBoolean(3, isDynamic);
            stmt.setInt(4, hosts);
            stmt.setInt(5, vms);
            stmt.setInt(6, cloudlets);
            stmt.setDouble(7, totalEnergy);
            stmt.setDouble(8, realExecTime);
            stmt.setDouble(9, cloudletExecTime);
            stmt.executeUpdate();
            System.out.println("Simulation summary saved!");
        } catch (SQLException e) {
            System.err.println("Failed to save summary: " + e.getMessage());
        }
    }

    public static void saveCloudlets(List<ResultsTable> cloudlets, String simulationId, String algorithm, boolean isDynamic) {
        String sql = """
        INSERT INTO simulation_results
        (simulation_id, cloudlet_id, status, vm_id, host_id, start_time, finish_time, exec_time, energy, algorithm, dynamic_scaling)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ResultsTable row : cloudlets) {
                stmt.setString(1, simulationId);
                stmt.setString(2, row.getCloudletId());
                stmt.setString(3, row.getStatus());
                stmt.setString(4, row.getVm());
                stmt.setString(5, row.getHost());
                stmt.setDouble(6, Double.parseDouble(row.getStartTime()));
                stmt.setDouble(7, Double.parseDouble(row.getFinishTime()));
                stmt.setDouble(8, Double.parseDouble(row.getExecTime()));
                stmt.setDouble(9, Double.parseDouble(row.getEnergy()));
                stmt.setString(10, algorithm);
                stmt.setBoolean(11, isDynamic);
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Cloudlet results saved!");
        } catch (SQLException e) {
            System.err.println("Failed to save cloudlet results: " + e.getMessage());
        }
    }

}
