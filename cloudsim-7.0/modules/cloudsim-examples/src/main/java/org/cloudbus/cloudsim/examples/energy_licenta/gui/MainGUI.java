package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.cloudbus.cloudsim.examples.energy_licenta.db.DatabaseManager;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SaveSimulation;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SchemaInitializer;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SimulationResult;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulatorDynamic;
import org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulatorNormal;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class MainGUI extends Application {

    private TableView<ResultsTable> resultsTable = new TableView<>();
    private Label summaryLabel = new Label("Total Energy: 0 \nAlgorithm: -");
    private String lastAlgorithm = "";
    private boolean lastDynamicScaling = false;
    private int lastNumHosts, lastNumVMs, lastNumCloudlets;
    private double lastTotalEnergy, lastRealExecTime, lastTotalExecTime;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CloudSim7G - Energy Simulation");

        SchemaInitializer.createTableIfNotExists();
        SchemaInitializer.createSummaryTableIfNotExists();


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
        RadioButton normalSimButton = new RadioButton("Normal");
        RadioButton dynamicSimButton = new RadioButton("Energy Efficient");
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
        //Button ecoButton = new Button("Eco Settings");
        Button saveButton = new Button("Save Results");
        Button loadButton = new Button("Load Results");


        runButton.setPrefWidth(127);
        runButton.setPrefHeight(50);

        suggestButton.setPrefWidth(150);
        suggestButton.setPrefHeight(50);

        saveButton.setPrefWidth(150);
        saveButton.setPrefHeight(50);

        loadButton.setPrefWidth(150);
        loadButton.setPrefHeight(50);

        runButton.setStyle("-fx-background-color: #5193c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius:8; -fx-cursor: hand; ");
        suggestButton.setStyle("-fx-background-color: #6c99c3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius:8; -fx-cursor: hand; ");
        //ecoButton.setStyle("-fx-background-color: #5193c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius:8; -fx-cursor: hand; ");
        saveButton.setStyle("-fx-background-color: #6c99c3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius:8; -fx-cursor: hand; ");
        loadButton.setStyle("-fx-background-color: #6c99c3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius:8; -fx-cursor: hand; ");


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
                new HBox(10, runButton),//, ecoButton),
                new HBox(10,suggestButton, saveButton, loadButton)

        );

        resultsTable = ResultsTable.buildTable();
        resultsTable.setPrefHeight(400);
        resultsTable.setMinWidth(600);
        resultsTable.setMaxWidth(600);

        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label summaryTitle = new Label("Summary:");
        summaryTitle.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");


        rightPane.getChildren().setAll(
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
                        "Total Energy: " + String.format("%.2f", totalEnergy) +  " kJ" +
                                "\nAlgorithm: " + selectedAlgo +
                                "\nExecution Time (real): " + String.format("%.2f", realExecTime) + " sec" +
                                "\nCloudlets Exec Time: " + String.format("%.2f", totalExecTime) + " sec"
                );

                // Memoram datele ultimei simulari
                lastAlgorithm = selectedAlgo;
                lastDynamicScaling = dynamicSimButton.isSelected();
                lastNumHosts = numHosts;
                lastNumVMs = numVMs;
                lastNumCloudlets = numCloudlets;
                lastTotalEnergy = totalEnergy;
                lastRealExecTime = realExecTime;
                lastTotalExecTime = totalExecTime;

                javafx.scene.chart.BarChart<String, Number> chart = createEnergyChart();

                rightPane.getChildren().setAll(
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

//        ecoButton.setOnAction(e -> {
//            vmRAMInput.setText("2");
//            vmMIPSInput.setText("2500");
//            pesNumberInput.setText("1");
//            showInfo("Eco Settings", "Applied 2 GB RAM / 2500 MIPS / 1 core");
//        });

        saveButton.setOnAction(e -> {
            if (lastAlgorithm == null || lastAlgorithm.isEmpty()) {
                showError("Run a simulation first before saving.");
                return;
            }

            String simulationId = UUID.randomUUID().toString();

            SaveSimulation.saveSummary(
                    simulationId,
                    lastAlgorithm,
                    lastDynamicScaling,
                    lastNumHosts,
                    lastNumVMs,
                    lastNumCloudlets,
                    lastTotalEnergy,
                    lastRealExecTime,
                    lastTotalExecTime
            );

            SaveSimulation.saveCloudlets(
                    resultsTable.getItems(),
                    simulationId,
                    lastAlgorithm,
                    lastDynamicScaling
            );

            showInfo("Success", "Simulation results saved to the database.");
        });

        loadButton.setOnAction(e -> showSummaryTableWindow());


        Scene scene = new Scene(mainLayout, 1100, 700);
        mainLayout.setStyle("-fx-background-color: #e0eaf5;");
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
        yAxis.setLabel("Energy in kJ");

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

    private void showSummaryTableWindow() {
        Stage stage = new Stage();
        stage.setTitle("Saved Simulations");

        TableView<SimulationSummaryLoad> table = new TableView<>();

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SimulationSummaryLoad, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));

        TableColumn<SimulationSummaryLoad, String> algoCol = new TableColumn<>("Algorithm");
        algoCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlgorithm()));

        TableColumn<SimulationSummaryLoad, Boolean> scalingCol = new TableColumn<>("Dynamic Scaling");
        scalingCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().isDynamicScaling()));

        TableColumn<SimulationSummaryLoad, Integer> hostsCol = new TableColumn<>("Hosts");
        hostsCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHosts()));

        TableColumn<SimulationSummaryLoad, Integer> vmsCol = new TableColumn<>("VMs");
        vmsCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getVms()));

        TableColumn<SimulationSummaryLoad, Integer> clCol = new TableColumn<>("Cloudlets");
        clCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCloudlets()));

        TableColumn<SimulationSummaryLoad, Double> energyCol = new TableColumn<>("Energy");
        energyCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTotalEnergy()));

        TableColumn<SimulationSummaryLoad, Double> realTimeCol = new TableColumn<>("Real Exec Time");
        realTimeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getRealExecTime()));

        TableColumn<SimulationSummaryLoad, Double> cloudletTimeCol = new TableColumn<>("Cloudlet Exec Time");
        cloudletTimeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCloudletExecTime()));

        TableColumn<SimulationSummaryLoad, Timestamp> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSimTimestamp()));

        TableColumn<SimulationSummaryLoad, Void> moreCol = new TableColumn<>("Details");
        TableColumn<SimulationSummaryLoad, Void> chartCol = new TableColumn<>("Chart");

        chartCol.setCellFactory(col -> new TableCell<>() {
            private final Button chartBtn = new Button("Show Chart");

            {
                chartBtn.setStyle("""
                    -fx-background-color: #6c99c3;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                """);
                chartBtn.setOnAction(e -> {
                    SimulationSummaryLoad summary = getTableView().getItems().get(getIndex());

                    // se creeaza graficul real pe baza cloudlet-urilor din DB
                    BarChart<String, Number> chart = createEnergyChartFromDb(summary.getId());

                    // recalculeaza totalEnergy si cloudletExecTime din simulation_results
                    List<SimulationResult> results = DatabaseManager.getResultsBySimulationId(summary.getId());

                    double totalEnergy = 0.0;
                    double cloudletExecTime = 0.0;
                    for (SimulationResult r : results) {
                        totalEnergy += r.getEnergy();
                        cloudletExecTime += r.getExecTime();
                    }

                    // apelează fereastra graficului cu valorile reale
                    showEnergyChartWindow(
                            chart,
                            summary.getAlgorithm(),
                            summary.isDynamicScaling(),
                            summary.getHosts(),
                            summary.getVms(),
                            summary.getCloudlets(),
                            totalEnergy,
                            summary.getRealExecTime(),
                            cloudletExecTime
                    );
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(chartBtn);
                }
            }
        });

        moreCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Show More");

            {
                btn.setStyle("""
                    -fx-background-color: #8fbac8;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                """);
                btn.setOnAction(e -> {
                    SimulationSummaryLoad summary = getTableView().getItems().get(getIndex());
                    showSimulationResultsWindow(summary.getId());
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });


        table.getColumns().addAll(
                idCol, algoCol, scalingCol, hostsCol, vmsCol, clCol,
                energyCol, realTimeCol, cloudletTimeCol, timeCol, moreCol, chartCol
        );

        table.getItems().addAll(DatabaseManager.getAllSimulationSummaries());

        Label titleLabel = new Label("Saved Simulations:");
        titleLabel.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");

        VBox layout = new VBox(10, titleLabel, table);
        layout.setStyle("-fx-background-color: #e0eaf5;");
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 1200, 500);
        stage.setScene(scene);
        stage.show();

    }

    private BarChart<String, Number> createEnergyChartFromDb(String simulationId) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("CloudletID");
        yAxis.setLabel("Energy");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Energy Consumption per Cloudlet");
        chart.setPrefHeight(250);
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<SimulationResult> results = DatabaseManager.getResultsBySimulationId(simulationId);

        for (SimulationResult result : results) {
            series.getData().add(new XYChart.Data<>(result.getCloudletId(), result.getEnergy()));
        }

        chart.getData().add(series);
        return chart;
    }


    private SimulationSummaryLoad findSummaryById(String simulationId, TableView<SimulationSummaryLoad> table) {
        return table.getItems()
                .stream()
                .filter(summary -> summary.getId().equals(simulationId))
                .findFirst()
                .orElse(null);
    }


    private void showSimulationResultsWindow(String simulationId) {
        Stage stage = new Stage();
        stage.setTitle("Simulation Results - ID " + simulationId);

        TableView<SimulationResult> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SimulationResult, String> cloudletIdCol = new TableColumn<>("Cloudlet ID");
        cloudletIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCloudletId()));

        TableColumn<SimulationResult, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<SimulationResult, String> vmIdCol = new TableColumn<>("VM ID");
        vmIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVmId()));

        TableColumn<SimulationResult, String> hostIdCol = new TableColumn<>("Host ID");
        hostIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHostId()));

        TableColumn<SimulationResult, Double> startTimeCol = new TableColumn<>("Start Time");
        startTimeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStartTime()));

        TableColumn<SimulationResult, Double> finishTimeCol = new TableColumn<>("Finish Time");
        finishTimeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFinishTime()));

        TableColumn<SimulationResult, Double> execTimeCol = new TableColumn<>("Exec Time");
        execTimeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getExecTime()));

        TableColumn<SimulationResult, Double> energyCol = new TableColumn<>("Energy");
        energyCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEnergy()));

        table.getColumns().addAll(cloudletIdCol, statusCol, vmIdCol, hostIdCol, startTimeCol, finishTimeCol, execTimeCol, energyCol);

        List<SimulationResult> results = DatabaseManager.getResultsBySimulationId(String.valueOf(simulationId));
        table.getItems().addAll(results);

        Label titleLabel = new Label("Results for Simulation ID: " + simulationId);
        titleLabel.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");

        VBox layout = new VBox(10, titleLabel, table);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #e0eaf5;");

        Scene scene = new Scene(layout, 1000, 500);
        stage.setScene(scene);
        stage.show();
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
                        "\n\nEnergy Efficient: " + (isDynamic ? "Enabled" : "Disabled") +
                        "\n\nTotal Energy: " + String.format("%.2f", totalEnergy) + " kJ" +
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
