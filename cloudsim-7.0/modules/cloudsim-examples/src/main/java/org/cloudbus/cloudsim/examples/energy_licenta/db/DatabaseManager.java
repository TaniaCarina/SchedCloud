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
        List<SimulationSummaryLoad> list = new ArrayList<>();
        String sql = "SELECT * FROM simulation_summary ORDER BY sim_timestamp DESC";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SimulationSummaryLoad(
                        rs.getInt("id"),
                        rs.getString("algorithm"),
                        rs.getBoolean("dynamic_scaling"),
                        rs.getInt("hosts"),
                        rs.getInt("vms"),
                        rs.getInt("cloudlets"),
                        rs.getDouble("total_energy"),
                        rs.getDouble("real_exec_time"),
                        rs.getDouble("cloudlet_exec_time"),
                        rs.getTimestamp("sim_timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch summaries: " + e.getMessage());
        }

        return list;
    }



}
