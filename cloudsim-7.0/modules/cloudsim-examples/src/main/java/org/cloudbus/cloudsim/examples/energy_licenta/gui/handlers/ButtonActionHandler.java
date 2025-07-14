package org.cloudbus.cloudsim.examples.energy_licenta.gui.handlers;

import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SaveSimulation;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.components.ResultsTable;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.components.ChartWindow;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.components.SummaryWindow;
import org.cloudbus.cloudsim.examples.energy_licenta.scaling.VMConsolidation;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergyEfficientMode;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.NormalMode;

public class ButtonActionHandler {

    public static class SimulationState {
        public String lastAlgorithm = "";
        public boolean lastDynamicScaling = false;
        public int lastNumHosts, lastNumVMs, lastNumCloudlets;
        public double lastTotalEnergy, lastRealExecTime, lastTotalExecTime;
    }

    public static void handleRunSimulation(
            SimulationState state,
            boolean isDynamic,
            int numHosts, int hostMIPS, int hostRAM,
            int numVMs, int vmMIPS, int vmRAM, long vmBW, long vmSize, int pesNumber,
            int numCloudlets, String algorithm,
            TableView<ResultsTable> table,
            Label summaryLabel
    ) {
        try {
            long startTime = System.currentTimeMillis();

            String results = isDynamic
                    ? EnergyEfficientMode.runSimulation(numHosts, hostMIPS, hostRAM, numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, algorithm)
                    : NormalMode.runSimulation(numHosts, hostMIPS, hostRAM, numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, algorithm);

            populateTableWithResults(table, results);

            long endTime = System.currentTimeMillis();
            double realExecTime = (endTime - startTime) / 1000.0;

            double totalExecTime = table.getItems().stream()
                    .mapToDouble(item -> parseDoubleSafe(item.getExecTime()))
                    .sum();

            double totalEnergy = table.getItems().stream()
                    .mapToDouble(item -> parseDoubleSafe(item.getEnergy()))
                    .sum();

            summaryLabel.setText(
                    "Total Energy: " + String.format("%.2f", totalEnergy) + " kJ" +
                            "\nAlgorithm: " + algorithm +
                            "\nExecution Time (real): " + String.format("%.2f", realExecTime) + " sec" +
                            "\nCloudlets Exec Time: " + String.format("%.2f", totalExecTime) + " sec" +
                            "\nStopped VMs: " + VMConsolidation.turnedOffVMs + " / " + numVMs
            );


            // actualizam starea ultimei simulari
            state.lastAlgorithm = algorithm;
            state.lastDynamicScaling = isDynamic;
            state.lastNumHosts = numHosts;
            state.lastNumVMs = numVMs;
            state.lastNumCloudlets = numCloudlets;
            state.lastTotalEnergy = totalEnergy;
            state.lastRealExecTime = realExecTime;
            state.lastTotalExecTime = totalExecTime;

            BarChart<String, Number> chart = ChartWindow.createEnergyChartFromTable(table);

            ChartWindow.showEnergyChartWindow(
                    chart, algorithm, isDynamic, numHosts, numVMs, numCloudlets,
                    totalEnergy, realExecTime, totalExecTime
            );

        } catch (Exception e) {
            showError("Please enter valid numeric values.");
        }
    }

    public static void handleSuggestResources(
            TextField vmsInput, TextField vmRAMInput, TextField vmMIPSInput, TextField pesInput,
            TextField hostRAMInput, TextField hostMIPSInput, TextField hostCoresInput,
            TextField hostsOutput
    ) {
        try {
            int numVMs = Integer.parseInt(vmsInput.getText());
            int vmRAM = Integer.parseInt(vmRAMInput.getText());
            int vmMIPS = Integer.parseInt(vmMIPSInput.getText());
            int vmCores = Integer.parseInt(pesInput.getText());

            int hostRAM = Integer.parseInt(hostRAMInput.getText());
            int hostMIPS = Integer.parseInt(hostMIPSInput.getText());
            int hostCores = Integer.parseInt(hostCoresInput.getText());

            int totalRAM = numVMs * vmRAM;
            int totalMIPS = numVMs * vmMIPS;
            int totalCores = numVMs * vmCores;

            int hostsByRAM = (int) Math.ceil((double) totalRAM / hostRAM);
            int hostsByMIPS = (int) Math.ceil((double) totalMIPS / hostMIPS);
            int hostsByCores = (int) Math.ceil((double) totalCores / hostCores);

            int suggested = Math.max(Math.max(hostsByRAM, hostsByMIPS), hostsByCores);
            hostsOutput.setText(String.valueOf(suggested));

            showInfo("Suggested Resources", "Total RAM: " + totalRAM + " GB\nMIPS: " + totalMIPS + "\nCores: " + totalCores + "\n\nSuggested Hosts: " + suggested);
        } catch (Exception e) {
            showError("Invalid numeric input.");
        }
    }

    public static void handleSaveSimulation(SimulationState state, TableView<ResultsTable> table) {
        if (state.lastAlgorithm == null || state.lastAlgorithm.isEmpty()) {
            showError("Run a simulation first before saving.");
            return;
        }

        String simulationId = java.util.UUID.randomUUID().toString();

        SaveSimulation.saveSummary(
                simulationId,
                state.lastAlgorithm,
                state.lastDynamicScaling,
                state.lastNumHosts,
                state.lastNumVMs,
                state.lastNumCloudlets,
                state.lastTotalEnergy,
                state.lastRealExecTime,
                state.lastTotalExecTime
        );

        SaveSimulation.saveCloudlets(
                table.getItems(),
                simulationId,
                state.lastAlgorithm,
                state.lastDynamicScaling
        );

        showInfo("Success", "Simulation results saved to the database.");
    }

    public static void handleLoadSimulation() {
        SummaryWindow.showSummaryTableWindow();
    }

    // --- Helpers ---
    private static void populateTableWithResults(TableView<ResultsTable> table, String results) {
        table.getItems().clear();
        String[] lines = results.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("-") || !line.contains("SUCCESS")) continue;
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 8) {
                table.getItems().add(new ResultsTable(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]));
            }
        }
    }

    private static double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    private static void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
