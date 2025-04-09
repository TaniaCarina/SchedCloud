package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulatorDynamic;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulatorNormal;

import java.io.PrintStream;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CloudSim GUI - Energy Simulation");

        Label modeLabel = new Label("Select Simulation Mode:");
        RadioButton normalSimButton = new RadioButton("Without Dynamic Scaling");
        RadioButton dynamicSimButton = new RadioButton("With Dynamic Scaling");

        ToggleGroup toggleGroup = new ToggleGroup();
        normalSimButton.setToggleGroup(toggleGroup);
        dynamicSimButton.setToggleGroup(toggleGroup);
        dynamicSimButton.setSelected(true);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #f4f4f4; -fx-font-size: 14px;");

        Label hostsLabel = new Label("Number of Hosts:");
        TextField hostsInput = new TextField("10");

        Label hostMIPSLabel = new Label("Host MIPS:");
        TextField hostMIPSInput = new TextField("10000");

        Label hostRAMLabel = new Label("Host RAM (GB):");
        TextField hostRAMInput = new TextField("32");
        hostRAMInput.setTooltip(new Tooltip("Total RAM per host in GB (1 GB = 1024 MB)"));

        Label hostCoresLabel = new Label("Cores per Host:");
        TextField hostCoresInput = new TextField("8");
        hostCoresInput.setTooltip(new Tooltip("Number of cores available per host"));

        Label vmsLabel = new Label("Number of VMs:");
        TextField vmsInput = new TextField("40");

        Label vmMIPSLabel = new Label("VM MIPS:");
        TextField vmMIPSInput = new TextField("2500");

        Label vmRAMLabel = new Label("VM RAM (GB):");
        TextField vmRAMInput = new TextField("2");
        vmRAMInput.setTooltip(new Tooltip("RAM per VM in GB (1 GB = 1024 MB)"));

        Label vmBWLabel = new Label("VM Bandwidth (MB/s):");
        TextField vmBWInput = new TextField("1000");

        Label vmSizeLabel = new Label("VM Storage (MB):");
        TextField vmSizeInput = new TextField("10000");

        Label pesNumberLabel = new Label("Cores per VM:");
        TextField pesNumberInput = new TextField("1");

        Label cloudletsLabel = new Label("Number of Cloudlets:");
        TextField cloudletsInput = new TextField("50");

        Label algoLabel = new Label("Scheduling Algorithm:");
        ComboBox<String> algoSelect = new ComboBox<>();
        algoSelect.getItems().addAll("RoundRobin", "ACO", "FCFS");
        algoSelect.setValue("RoundRobin");

        Button runButton = new Button("Run Simulation");
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");

        Button suggestButton = new Button("Suggest Resources");
        suggestButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");

        suggestButton.setOnAction(e -> {
            try {
                int numVMs = Integer.parseInt(vmsInput.getText());
                int vmRAMGB = Integer.parseInt(vmRAMInput.getText());
                int vmMIPS = Integer.parseInt(vmMIPSInput.getText());
                int vmCores = Integer.parseInt(pesNumberInput.getText());

                int hostRAMGB = Integer.parseInt(hostRAMInput.getText());
                int hostMIPS = Integer.parseInt(hostMIPSInput.getText());
                int hostCores = Integer.parseInt(hostCoresInput.getText());

                int totalRAMGB = numVMs * vmRAMGB;
                int totalMIPS = numVMs * vmMIPS;
                int totalCores = numVMs * vmCores;

                int hostsByRAM = (int) Math.ceil((double) totalRAMGB / hostRAMGB);
                int hostsByMIPS = (int) Math.ceil((double) totalMIPS / hostMIPS);
                int hostsByCores = (int) Math.ceil((double) totalCores / hostCores);

                int suggestedHosts = Math.max(Math.max(hostsByRAM, hostsByMIPS), hostsByCores);

                hostsInput.setText(String.valueOf(suggestedHosts));
                hostRAMInput.setText(String.valueOf(hostRAMGB));

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suggested Resources");
                alert.setHeaderText("Based on VM requirements:");
                alert.setContentText(
                        "Total RAM required: " + totalRAMGB + " GB\n" +
                                "Total MIPS required: " + totalMIPS + "\n" +
                                "Total cores required: " + totalCores + "\n\n" +
                                "Suggested number of hosts: " + suggestedHosts +
                                " (based on RAM, MIPS, and core limits)"
                );
                alert.showAndWait();
            } catch (NumberFormatException ex) {
                showError("Please enter valid numeric values for VM and Host settings.");
            }
        });

        runButton.setOnAction(e -> {
            try {
                int numHosts = Integer.parseInt(hostsInput.getText());
                int hostMIPS = Integer.parseInt(hostMIPSInput.getText());
                int hostRAM = Integer.parseInt(hostRAMInput.getText()) * 1024;

                int numVMs = Integer.parseInt(vmsInput.getText());
                int vmMIPS = Integer.parseInt(vmMIPSInput.getText());
                int vmRAM = Integer.parseInt(vmRAMInput.getText()) * 1024;
                long vmBW = Long.parseLong(vmBWInput.getText());
                long vmSize = Long.parseLong(vmSizeInput.getText());
                int pesNumber = Integer.parseInt(pesNumberInput.getText());
                int numCloudlets = Integer.parseInt(cloudletsInput.getText());
                String selectedAlgo = algoSelect.getValue();

                int totalVMRam = numVMs * vmRAM;
                int totalHostRam = numHosts * hostRAM;

                if (totalVMRam > totalHostRam) {
                    showError("Insufficient total RAM. Required: " + totalVMRam + " MB, Available: " + totalHostRam + " MB");
                    return;
                }

                TextArea consoleOutput = new TextArea();
                consoleOutput.setEditable(false);
                consoleOutput.setWrapText(true);
                consoleOutput.setPrefHeight(300);
                consoleOutput.setStyle("-fx-font-family: monospace; -fx-control-inner-background: black; -fx-text-fill: white;");

                PrintStream ps = new PrintStream(new ConsoleOutputStream(consoleOutput), true);
                System.setOut(ps);
                System.setErr(ps);

                String results;
                if (dynamicSimButton.isSelected()) {
                    results = EnergySimulatorDynamic.runSimulation(numHosts, hostMIPS, hostRAM, numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, selectedAlgo);
                } else {
                    results = EnergySimulatorNormal.runSimulation(numHosts, hostMIPS, hostRAM, numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, selectedAlgo);
                }

                showResultsWindow(results, consoleOutput);

            } catch (NumberFormatException ex) {
                showError("Please enter valid numeric values.");
            }
        });

        int row = 0;
        grid.add(modeLabel, 0, row);
        grid.add(normalSimButton, 1, row);
        grid.add(dynamicSimButton, 2, row++);

        grid.add(new Label("----- Hosts Configuration -----"), 0, row++, 2, 1);
        grid.add(hostsLabel, 0, row);
        grid.add(hostsInput, 1, row++);
        grid.add(hostMIPSLabel, 0, row);
        grid.add(hostMIPSInput, 1, row++);
        grid.add(hostRAMLabel, 0, row);
        grid.add(hostRAMInput, 1, row++);
        grid.add(hostCoresLabel, 0, row);
        grid.add(hostCoresInput, 1, row++);

        grid.add(new Label("----- VMs Configuration -----"), 0, row++, 2, 1);
        grid.add(vmsLabel, 0, row);
        grid.add(vmsInput, 1, row++);
        grid.add(vmMIPSLabel, 0, row);
        grid.add(vmMIPSInput, 1, row++);
        grid.add(vmRAMLabel, 0, row);
        grid.add(vmRAMInput, 1, row++);
        grid.add(vmBWLabel, 0, row);
        grid.add(vmBWInput, 1, row++);
        grid.add(vmSizeLabel, 0, row);
        grid.add(vmSizeInput, 1, row++);
        grid.add(pesNumberLabel, 0, row);
        grid.add(pesNumberInput, 1, row++);

        grid.add(new Label("----- Cloudlets Configuration -----"), 0, row++, 2, 1);
        grid.add(cloudletsLabel, 0, row);
        grid.add(cloudletsInput, 1, row++);

        grid.add(new Label("----- Scheduling Algorithm -----"), 0, row++, 2, 1);
        grid.add(algoLabel, 0, row);
        grid.add(algoSelect, 1, row++);

        grid.add(runButton, 0, row);
        grid.add(suggestButton, 1, row++);

        Scene scene = new Scene(grid, 650, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Configuration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showResultsWindow(String results, TextArea consoleArea) {
        Stage resultsStage = new Stage();
        resultsStage.setTitle("Simulation Results");
        resultsStage.initModality(Modality.APPLICATION_MODAL);
        resultsStage.setMinWidth(700);
        resultsStage.setMinHeight(500);

        TextArea resultsArea = new TextArea(results);
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-font-size: 14px;");
        closeButton.setOnAction(e -> resultsStage.close());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Simulation Output:"), consoleArea, new Label("Summary:"), resultsArea, closeButton);

        Scene scene = new Scene(layout);
        resultsStage.setScene(scene);
        resultsStage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




///doar tabelul
//    private void showResultsWindow(String results) {
//        Stage resultsStage = new Stage();
//        resultsStage.setTitle("Simulation Results");
//        resultsStage.initModality(Modality.APPLICATION_MODAL);
//        resultsStage.setMinWidth(600);
//        resultsStage.setMinHeight(400);
//
//        TextArea resultsArea = new TextArea(results);
//        resultsArea.setEditable(false);
//        resultsArea.setWrapText(true);
//        resultsArea.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
//
//        Button closeButton = new Button("Close");
//        closeButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-font-size: 14px;");
//        closeButton.setOnAction(e -> resultsStage.close());
//
//        VBox layout = new VBox(10);
//        layout.setPadding(new Insets(20));
//        layout.getChildren().addAll(new Label("Simulation Results:"), resultsArea, closeButton);
//
//        Scene scene = new Scene(layout, 600, 400);
//        resultsStage.setScene(scene);
//        resultsStage.showAndWait();
//    }





//    private void runPythonAnimation() {
//        try {
//            String pythonPath = "C:\\Users\\tania\\AppData\\Local\\Programs\\Python\\Python311\\python.exe";
//            String scriptPath = "C:\\cloudsim-7.0\\ShowSimulation\\animation.py";
//
//            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath);
//            pb.inheritIO();
//            Process process = pb.start();
//            process.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
