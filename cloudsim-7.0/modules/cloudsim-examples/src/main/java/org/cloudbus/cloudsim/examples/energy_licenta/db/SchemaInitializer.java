package org.cloudbus.cloudsim.examples.energy_licenta.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.cloudbus.cloudsim.examples.energy_licenta.db.DatabaseManager.connect;

public class SchemaInitializer {
    public static void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS simulation_results (
                id INT AUTO_INCREMENT PRIMARY KEY,
                cloudlet_id VARCHAR(50),
                status VARCHAR(20),
                vm_id VARCHAR(20),
                host_id VARCHAR(20),
                start_time DOUBLE,
                finish_time DOUBLE,
                exec_time DOUBLE,
                energy DOUBLE,
                algorithm VARCHAR(50),
                dynamic_scaling BOOLEAN,
                sim_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to create table: " + e.getMessage());
        }
    }

    public static void createSummaryTableIfNotExists() {
        String sql = """
        CREATE TABLE IF NOT EXISTS simulation_summary (
            id INT AUTO_INCREMENT PRIMARY KEY,
            algorithm VARCHAR(50),
            dynamic_scaling BOOLEAN,
            hosts INT,
            vms INT,
            cloudlets INT,
            total_energy DOUBLE,
            real_exec_time DOUBLE,
            cloudlet_exec_time DOUBLE,
            sim_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Summary table checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to create summary table: " + e.getMessage());
        }
    }
}
