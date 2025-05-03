package org.cloudbus.cloudsim.examples.energy_licenta.db;

import org.cloudbus.cloudsim.examples.energy_licenta.gui.SimulationSummaryLoad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/cloudsim_db";
    private static final String USER = "root";
    private static final String PASSWORD = "T.2486";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static List<SimulationSummaryLoad> getAllSimulationSummaries() {
        List<SimulationSummaryLoad> summaries = new ArrayList<>();
        String sql = "SELECT * FROM simulation_summary";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SimulationSummaryLoad summary = new SimulationSummaryLoad(
                        rs.getString("simulation_id"), // UUID ca String
                        rs.getString("algorithm"),
                        rs.getBoolean("dynamic_scaling"),
                        rs.getInt("hosts"),
                        rs.getInt("vms"),
                        rs.getInt("cloudlets"),
                        rs.getDouble("total_energy"),
                        rs.getDouble("real_exec_time"),
                        rs.getDouble("cloudlet_exec_time"),
                        rs.getTimestamp("sim_timestamp")
                );
                summaries.add(summary);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch summaries: " + e.getMessage());
        }

        return summaries;
    }


    public static List<SimulationResult> getResultsBySimulationId(String simulationId) {
        List<SimulationResult> results = new ArrayList<>();
        String sql = "SELECT * FROM simulation_results WHERE simulation_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, simulationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SimulationResult result = new SimulationResult(
                        rs.getString("cloudlet_id"),
                        rs.getString("status"),
                        rs.getString("vm_id"),
                        rs.getString("host_id"),
                        rs.getDouble("start_time"),
                        rs.getDouble("finish_time"),
                        rs.getDouble("exec_time"),
                        rs.getDouble("energy")
                );
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching simulation results: " + e.getMessage());
        }

        return results;
    }




}
