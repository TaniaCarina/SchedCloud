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


public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CloudSim GUI - Energy Simulation");

        // Alegerea tipului de simulare
        Label modeLabel = new Label("Select Simulation Mode:");
        RadioButton normalSimButton = new RadioButton("Without Dynamic Scaling");
        RadioButton dynamicSimButton = new RadioButton("With Dynamic Scaling");

        ToggleGroup toggleGroup = new ToggleGroup();
        normalSimButton.setToggleGroup(toggleGroup);
        dynamicSimButton.setToggleGroup(toggleGroup);
        dynamicSimButton.setSelected(true); // Implicit, simularea cu scalare dinamică e selectată

        // GridPane pentru layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #f4f4f4; -fx-font-size: 14px;");

        // Secțiunea pentru HOSTS
        Label hostsLabel = new Label("Number of Hosts:");
        TextField hostsInput = new TextField("10");

        Label hostMIPSLabel = new Label("Host MIPS:");
        TextField hostMIPSInput = new TextField("10000");

        Label hostRAMLabel = new Label("Host RAM (MB):");
        TextField hostRAMInput = new TextField("32000");

        // Secțiunea pentru VMs
        Label vmsLabel = new Label("Number of VMs:");
        TextField vmsInput = new TextField("40");

        Label vmMIPSLabel = new Label("VM MIPS:");
        TextField vmMIPSInput = new TextField("2500");

        Label vmRAMLabel = new Label("VM RAM (MB):");
        TextField vmRAMInput = new TextField("2048");

        Label vmBWLabel = new Label("VM Bandwidth (MB/s):");
        TextField vmBWInput = new TextField("1000");

        Label vmSizeLabel = new Label("VM Storage (MB):");
        TextField vmSizeInput = new TextField("10000");

        Label pesNumberLabel = new Label("Cores per VM:");
        TextField pesNumberInput = new TextField("1");

        // Secțiunea pentru Cloudlets
        Label cloudletsLabel = new Label("Number of Cloudlets:");
        TextField cloudletsInput = new TextField("50");

        // Dropdown pentru Algoritmul de Scheduling
        Label algoLabel = new Label("Scheduling Algorithm:");
        ComboBox<String> algoSelect = new ComboBox<>();
        algoSelect.getItems().addAll("RoundRobin", "ACO", "FCFS");
        algoSelect.setValue("RoundRobin");

        // Buton pentru rularea simulării
        Button runButton = new Button("Run Simulation");
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");

        runButton.setOnAction(e -> {
            int numHosts = Integer.parseInt(hostsInput.getText());
            int hostMIPS = Integer.parseInt(hostMIPSInput.getText());
            int hostRAM = Integer.parseInt(hostRAMInput.getText());
            int numVMs = Integer.parseInt(vmsInput.getText());
            int vmMIPS = Integer.parseInt(vmMIPSInput.getText());
            int vmRAM = Integer.parseInt(vmRAMInput.getText());
            long vmBW = Long.parseLong(vmBWInput.getText());
            long vmSize = Long.parseLong(vmSizeInput.getText());
            int pesNumber = Integer.parseInt(pesNumberInput.getText());
            int numCloudlets = Integer.parseInt(cloudletsInput.getText());
            String selectedAlgo = algoSelect.getValue();

            String results;
            if (dynamicSimButton.isSelected()) {
                results = EnergySimulatorDynamic.runSimulation(numHosts, hostMIPS, hostRAM, numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, selectedAlgo);
            } else {
                results = EnergySimulatorNormal.runSimulation(
                        numHosts, hostMIPS, hostRAM, numVMs, vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, selectedAlgo
                );
            }

            showResultsWindow(results);
        });

        // Adaugă elementele în GridPane
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

        grid.add(runButton, 0, row++, 2, 1);

        // Setează scena și afișează fereastra
        Scene scene = new Scene(grid, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showResultsWindow(String results) {
        Stage resultsStage = new Stage();
        resultsStage.setTitle("Simulation Results");
        resultsStage.initModality(Modality.APPLICATION_MODAL);
        resultsStage.setMinWidth(600);
        resultsStage.setMinHeight(400);

        TextArea resultsArea = new TextArea(results);
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-font-size: 14px;");
        closeButton.setOnAction(e -> resultsStage.close());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new Label("Simulation Results:"), resultsArea, closeButton);

        Scene scene = new Scene(layout, 600, 400);
        resultsStage.setScene(scene);
        resultsStage.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}



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
