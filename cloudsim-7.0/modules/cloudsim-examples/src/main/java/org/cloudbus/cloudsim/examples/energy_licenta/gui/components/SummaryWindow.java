package org.cloudbus.cloudsim.examples.energy_licenta.gui.components;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cloudbus.cloudsim.examples.energy_licenta.db.DatabaseManager;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SimulationResult;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.SimulationSummaryLoad;

import java.sql.Timestamp;
import java.util.List;

public class SummaryWindow {

    public static void showSummaryTableWindow() {
        Stage stage = new Stage();
        stage.setTitle("Saved Simulations");

        TableView<SimulationSummaryLoad> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Coloane
        table.getColumns().addAll(
                column("ID", data -> data.getId()),
                column("Algorithm", data -> data.getAlgorithm()),
                boolColumn("Dynamic Scaling", SimulationSummaryLoad::isDynamicScaling),
                intColumn("Hosts", SimulationSummaryLoad::getHosts),
                intColumn("VMs", SimulationSummaryLoad::getVms),
                intColumn("Cloudlets", SimulationSummaryLoad::getCloudlets),
                doubleColumn("Energy", SimulationSummaryLoad::getTotalEnergy),
                doubleColumn("Real Exec Time", SimulationSummaryLoad::getRealExecTime),
                doubleColumn("Cloudlet Exec Time", SimulationSummaryLoad::getCloudletExecTime),
                timestampColumn("Timestamp", SimulationSummaryLoad::getSimTimestamp),
                actionColumn("Details", "Show More", summary -> ResultsTable.showSimulationResultsWindow(summary.getId())),
                actionColumn("Chart", "Show Chart", summary -> {
                    BarChart<String, Number> chart = ChartWindow.createEnergyChartFromDb(summary.getId());
                    List<SimulationResult> results = DatabaseManager.getResultsBySimulationId(summary.getId());

                    double totalEnergy = results.stream().mapToDouble(SimulationResult::getEnergy).sum();
                    double cloudletExecTime = results.stream().mapToDouble(SimulationResult::getExecTime).sum();

                    ChartWindow.showEnergyChartWindow(
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
                })

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

    // === Helperi pentru coloane ===
    private static TableColumn<SimulationSummaryLoad, String> column(String title, javafx.util.Callback<SimulationSummaryLoad, String> extractor) {
        TableColumn<SimulationSummaryLoad, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(extractor.call(data.getValue())));
        return col;
    }

    private static TableColumn<SimulationSummaryLoad, Boolean> boolColumn(String title, javafx.util.Callback<SimulationSummaryLoad, Boolean> extractor) {
        TableColumn<SimulationSummaryLoad, Boolean> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(extractor.call(data.getValue())));
        return col;
    }

    private static TableColumn<SimulationSummaryLoad, Integer> intColumn(String title, javafx.util.Callback<SimulationSummaryLoad, Integer> extractor) {
        TableColumn<SimulationSummaryLoad, Integer> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(extractor.call(data.getValue())));
        return col;
    }

    private static TableColumn<SimulationSummaryLoad, Double> doubleColumn(String title, javafx.util.Callback<SimulationSummaryLoad, Double> extractor) {
        TableColumn<SimulationSummaryLoad, Double> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(extractor.call(data.getValue())));
        return col;
    }

    private static TableColumn<SimulationSummaryLoad, Timestamp> timestampColumn(String title, javafx.util.Callback<SimulationSummaryLoad, Timestamp> extractor) {
        TableColumn<SimulationSummaryLoad, Timestamp> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(extractor.call(data.getValue())));
        return col;
    }

    private static TableColumn<SimulationSummaryLoad, Void> actionColumn(String title, String buttonLabel, java.util.function.Consumer<SimulationSummaryLoad> action) {
        TableColumn<SimulationSummaryLoad, Void> col = new TableColumn<>(title);
        col.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button(buttonLabel);

            {
                btn.setStyle("""
                    -fx-background-color: #6c99c3;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                """);
                btn.setOnAction(e -> {
                    SimulationSummaryLoad summary = getTableView().getItems().get(getIndex());
                    action.accept(summary);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        return col;
    }
}
