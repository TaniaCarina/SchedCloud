package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

        // Label & Input pentru numărul de Host-uri
        Label hostsLabel = new Label("Number of Hosts:");
        TextField hostsInput = new TextField("10");

        // Label & Input pentru numărul de VM-uri
        Label vmsLabel = new Label("Number of VMs:");
        TextField vmsInput = new TextField("40");

        // Label & Input pentru numărul de Cloudlets
        Label cloudletsLabel = new Label("Number of Cloudlets:");
        TextField cloudletsInput = new TextField("50");

        // Dropdown pentru Algoritmul de Scheduling
        Label algoLabel = new Label("Scheduling Algorithm:");
        ComboBox<String> algoSelect = new ComboBox<>();
        algoSelect.getItems().addAll("RoundRobin", "ACO", "FCFS");
        algoSelect.setValue("RoundRobin");

        // Buton pentru rularea simulării
        Button runButton = new Button("Run Simulation");
        runButton.setOnAction(e -> {
            int numHosts = Integer.parseInt(hostsInput.getText());
            int numVMs = Integer.parseInt(vmsInput.getText());
            int numCloudlets = Integer.parseInt(cloudletsInput.getText());
            String selectedAlgo = algoSelect.getValue();

            // Verifică tipul de simulare selectat
            if (dynamicSimButton.isSelected()) {
                EnergySimulatorDynamic.runSimulation(numHosts, numVMs, numCloudlets, selectedAlgo);
            } else {
                EnergySimulatorNormal.runSimulation(numHosts, numVMs, numCloudlets, selectedAlgo);
            }

            // Afișează mesaj de succes
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Simulation Completed! Results saved.", ButtonType.OK);
            alert.showAndWait();
        });

        // Adaugă elementele în GridPane
        grid.add(modeLabel, 0, 0);
        grid.add(normalSimButton, 1, 0);
        grid.add(dynamicSimButton, 2, 0);
        grid.add(hostsLabel, 0, 1);
        grid.add(hostsInput, 1, 1);
        grid.add(vmsLabel, 0, 2);
        grid.add(vmsInput, 1, 2);
        grid.add(cloudletsLabel, 0, 3);
        grid.add(cloudletsInput, 1, 3);
        grid.add(algoLabel, 0, 4);
        grid.add(algoSelect, 1, 4);
        grid.add(runButton, 0, 5, 2, 1);

        // Setează scena și afișează fereastra
        Scene scene = new Scene(grid, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
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
