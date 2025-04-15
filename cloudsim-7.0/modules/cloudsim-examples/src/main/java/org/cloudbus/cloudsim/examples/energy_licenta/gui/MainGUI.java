package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

import javafx.util.Duration;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulatorDynamic;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulatorNormal;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class MainGUI extends Application {

    //private final TextArea consoleOutput = new TextArea();
    private TableView<ResultsTable> resultsTable = new TableView<>();
    private Label summaryLabel = new Label("Total Energy: 0 \nAlgorithm: -");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CloudSim7G - Energy Simulation");

        VBox leftPane = new VBox(12);
        leftPane.setPadding(new Insets(20));
        leftPane.setPrefWidth(440);

        VBox rightPane = new VBox(12);
        rightPane.setPadding(new Insets(20));
        rightPane.setPrefWidth(600);

        HBox mainLayout = new HBox(leftPane, rightPane);
        mainLayout.setStyle("-fx-background-color: #e0eaf5;");
        mainLayout.setSpacing(20);

        String labelStyle = "-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;";
        String inputStyle = "-fx-background-color: #ffffff; -fx-text-fill: #0D1B2A;";

        summaryLabel.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label modeLabel = new Label("Select Simulation Mode:");
        modeLabel.setStyle(labelStyle);
        RadioButton normalSimButton = new RadioButton("Without Dynamic Scaling");
        RadioButton dynamicSimButton = new RadioButton("With Dynamic Scaling");
        ToggleGroup toggleGroup = new ToggleGroup();
        normalSimButton.setToggleGroup(toggleGroup);
        dynamicSimButton.setToggleGroup(toggleGroup);
        dynamicSimButton.setSelected(true);

        TextField hostsInput = styledField("10", inputStyle);
        TextField hostMIPSInput = styledField("10000", inputStyle);
        TextField hostRAMInput = styledField("32", inputStyle);
        TextField hostCoresInput = styledField("8", inputStyle);
        TextField vmsInput = styledField("40", inputStyle);
        TextField vmMIPSInput = styledField("2500", inputStyle);
        TextField vmRAMInput = styledField("2", inputStyle);
        TextField vmBWInput = styledField("1000", inputStyle);
        TextField vmSizeInput = styledField("10000", inputStyle);
        TextField pesNumberInput = styledField("1", inputStyle);
        TextField cloudletsInput = styledField("50", inputStyle);

        ComboBox<String> algoSelect = new ComboBox<>();
        algoSelect.getItems().addAll("MinLengthRoundRobin", "RoundRobin", "ACO", "FCFS", "Random", "LJF", "MinMin", "MaxMin", "PSO", "Genetic");
        algoSelect.setValue("MinLengthRoundRobin");

        Button runButton = new Button("Run Simulation");
        Button suggestButton = new Button("Suggest Resources");
        Button ecoButton = new Button("Eco Settings");

        runButton.setStyle("-fx-background-color: #1e81b0; -fx-text-fill: white;");
        suggestButton.setStyle("-fx-background-color: #5193c7; -fx-text-fill: white;");
        ecoButton.setStyle("-fx-background-color: #88c0d0; -fx-text-fill: #0D1B2A;");

        leftPane.getChildren().addAll(
                modeLabel, normalSimButton, dynamicSimButton,
                labeledBox("Number of Hosts:", hostsInput, labelStyle),
                labeledBox("Host MIPS:", hostMIPSInput, labelStyle),
                labeledBox("Host RAM (GB):", hostRAMInput, labelStyle),
                labeledBox("Cores per Host:", hostCoresInput, labelStyle),
                labeledBox("Number of VMs:", vmsInput, labelStyle),
                labeledBox("VM MIPS:", vmMIPSInput, labelStyle),
                labeledBox("VM RAM (GB):", vmRAMInput, labelStyle),
                labeledBox("VM Bandwidth (MB/s):", vmBWInput, labelStyle),
                labeledBox("VM Storage (MB):", vmSizeInput, labelStyle),
                labeledBox("Cores per VM:", pesNumberInput, labelStyle),
                labeledBox("Number of Cloudlets (Tasks):", cloudletsInput, labelStyle),
                labeledBox("Scheduling Algorithm:", algoSelect, labelStyle),
                new HBox(10, runButton, suggestButton, ecoButton)
        );

        //consoleOutput.setEditable(false);
        //consoleOutput.setPrefHeight(300);
       // consoleOutput.setStyle("-fx-font-family: Consolas; -fx-control-inner-background: #0D1B2A; -fx-text-fill: white;");

        resultsTable = ResultsTable.buildTable();
        resultsTable.setPrefHeight(400);
        resultsTable.setMinWidth(600);
        resultsTable.setMaxWidth(600);


        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

//        Label outputLabel = new Label("Simulation Output:");
//        outputLabel.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label summaryTitle = new Label("Summary:");
        summaryTitle.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");



        rightPane.getChildren().setAll(
               // outputLabel,
               // consoleOutput,
                summaryTitle,
                resultsTable
        );


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

               // PrintStream ps = new PrintStream(new ConsoleOutputStream(consoleOutput), true);
               //System.setOut(ps);
               //System.setErr(ps);

                String results;

                long startTime = System.currentTimeMillis();

                if (dynamicSimButton.isSelected()) {
                    results = EnergySimulatorDynamic.runSimulation(numHosts, hostMIPS, hostRAM, numVMs,
                            vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, selectedAlgo);
                } else {
                    results = EnergySimulatorNormal.runSimulation(numHosts, hostMIPS, hostRAM, numVMs,
                            vmMIPS, vmRAM, vmBW, vmSize, pesNumber, numCloudlets, selectedAlgo);
                }

                populateTableWithResults(results);

                long endTime = System.currentTimeMillis();
                double realExecTime = (endTime - startTime) / 1000.0;

                double totalExecTime = resultsTable.getItems().stream()
                        .mapToDouble(item -> {
                            try { return Double.parseDouble(item.getExecTime()); }
                            catch (NumberFormatException ex) { return 0; }
                        })
                        .sum();


                double totalEnergy = resultsTable.getItems().stream()
                        .mapToDouble(item -> {
                            try { return Double.parseDouble(item.getEnergy()); }
                            catch (NumberFormatException exception) { return 0; }
                        })
                        .sum();

                summaryLabel.setText(
                        "Total Energy: " + String.format("%.2f", totalEnergy) +
                                "\nAlgorithm: " + selectedAlgo +
                                "\nExecution Time (real): " + String.format("%.2f", realExecTime) + " sec" +
                                "\nCloudlets Exec Time: " + String.format("%.2f", totalExecTime) + " sec"
                );

                javafx.scene.chart.BarChart<String, Number> chart = createEnergyChart();

                rightPane.getChildren().setAll(
                       // outputLabel,
                       // consoleOutput,
                        summaryTitle,
                        resultsTable,
                        summaryLabel
                );

                showEnergyChartWindow(
                        chart,
                        selectedAlgo,
                        dynamicSimButton.isSelected(),
                        numHosts,
                        numVMs,
                        numCloudlets,
                        totalEnergy,
                        realExecTime,
                        totalExecTime
                );



            } catch (Exception ex) {
                showError("Please enter valid numeric values.");
            }
        });

        suggestButton.setOnAction(e -> {
            try {
                int numVMs = Integer.parseInt(vmsInput.getText());
                int vmRAM = Integer.parseInt(vmRAMInput.getText());
                int vmMIPS = Integer.parseInt(vmMIPSInput.getText());
                int vmCores = Integer.parseInt(pesNumberInput.getText());
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
                hostsInput.setText(String.valueOf(suggested));

                showInfo("Suggested Resources", "Total RAM: " + totalRAM + " GB\nMIPS: " + totalMIPS + "\nCores: " + totalCores + "\n\nSuggested Hosts: " + suggested);

            } catch (Exception ex) {
                showError("Invalid numeric input.");
            }
        });

        ecoButton.setOnAction(e -> {
            vmRAMInput.setText("2");
            vmMIPSInput.setText("2500");
            pesNumberInput.setText("1");
            showInfo("Eco Settings", "Applied 2 GB RAM / 2500 MIPS / 1 core");
        });

        Scene scene = new Scene(mainLayout, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void populateTableWithResults(String results) {
        resultsTable.getItems().clear();
        String[] lines = results.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("-") || line.contains("Status") || !line.contains("SUCCESS"))
                continue;
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 8) {
                resultsTable.getItems().add(new ResultsTable(
                        parts[0], parts[1], parts[2], parts[3],
                        parts[4], parts[5], parts[6], parts[7]
                ));
            }
        }
    }

    private javafx.scene.chart.BarChart<String, Number> createEnergyChart() {
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        xAxis.setLabel("CloudletID");
        yAxis.setLabel("Energy");

        javafx.scene.chart.BarChart<String, Number> chart = new javafx.scene.chart.BarChart<>(xAxis, yAxis);
        chart.setTitle("Energy Consumption per Cloudlet");
        chart.setPrefHeight(250);
        chart.setLegendVisible(false);

        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();

        for (ResultsTable row : resultsTable.getItems()) {
            try {
                double energy = Double.parseDouble(row.getEnergy());
                series.getData().add(new javafx.scene.chart.XYChart.Data<>(row.getCloudletId(), energy));
            } catch (NumberFormatException ignored) {}
        }

        chart.getData().add(series);
        return chart;
    }


    private TextField styledField(String text, String style) {
        TextField field = new TextField(text);
        field.setStyle(style);
        return field;
    }

    private HBox labeledBox(String labelText, Control control, String style) {
        Label label = new Label(labelText);
        label.setStyle(style);
        return new HBox(10, label, control);
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showEnergyChartWindow(BarChart<String, Number> chart, String algorithm, boolean isDynamic,
            int numHosts,
            int numVMs,
            int numCloudlets,
            double totalEnergy,
            double realExecTime,
            double totalExecTime
    )
    {
        chart.setStyle("-fx-bar-fill: #ec6ba1;");
        chart.setPrefSize(850, 500);

        Label chartTitle = new Label("Energy Consumption Chart:");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0D1B2A;");

        Label leftInfo = new Label(
                "Algorithm: " + algorithm +
                        "\n\nDynamic Scaling: " + (isDynamic ? "Enabled" : "Disabled") +
                        "\n\nTotal Energy: " + String.format("%.2f", totalEnergy) +
                        "\n\nExecution Time (real): " + String.format("%.2f", realExecTime) + " sec"

        );
        leftInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #0D1B2A; -fx-font-weight: bold;");

        Label rightInfo = new Label(
                "Number of Hosts: " + numHosts +
                        "\n\nNumber of VMs: " + numVMs +
                        "\n\nNumber of Cloudlets: " + numCloudlets +
                        "\n\nCloudlets Exec Time: " + String.format("%.2f", totalExecTime) + " sec"
        );
        rightInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #0D1B2A; -fx-font-weight: bold;");

        HBox infoBox = new HBox(150);
        infoBox.getChildren().addAll(leftInfo, rightInfo);
        infoBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #e0eaf5;");
        layout.getChildren().addAll(chartTitle, chart, infoBox);

        Scene scene = new Scene(layout, 1020, 800);
        Stage chartStage = new Stage();
        chartStage.setTitle("Energy Chart");
        chartStage.setScene(scene);
        chartStage.show();


        String scaling = isDynamic ? "DS" : "NDS"; // DS = dynamic scaling, NDS = no dynamic scaling
        String filename = String.format("chart_%s_%dVM_%dCL_%s.png", scaling, numVMs, numCloudlets, algorithm.replaceAll("\\s+", ""));

        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(event -> {
            WritableImage image = layout.snapshot(new SnapshotParameters(), null);
            File file = new File(filename);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                System.out.println("Chart saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        delay.play();

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
