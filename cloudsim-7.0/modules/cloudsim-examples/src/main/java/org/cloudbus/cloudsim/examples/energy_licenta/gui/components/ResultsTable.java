package org.cloudbus.cloudsim.examples.energy_licenta.gui.components;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.cloudbus.cloudsim.examples.energy_licenta.db.DatabaseManager;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SimulationResult;

import java.util.List;


public class ResultsTable {
    private final String cloudletId;
    private final String status;
    private final String vm;
    private final String host;
    private final String startTime;
    private final String finishTime;
    private final String execTime;
    private final String energy;

    public ResultsTable(String cloudletId, String status, String vm, String host, String startTime, String finishTime, String execTime, String energy) {
        this.cloudletId = cloudletId;
        this.status = status;
        this.vm = vm;
        this.host = host;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.execTime = execTime;
        this.energy = energy;
    }

    public String getCloudletId() { return cloudletId; }
    public String getStatus() { return status; }
    public String getVm() { return vm; }
    public String getHost() { return host; }
    public String getStartTime() { return startTime; }
    public String getFinishTime() { return finishTime; }
    public String getExecTime() { return execTime; }
    public String getEnergy() { return energy; }

    public static void showSimulationResultsWindow(String simulationId) {
        Stage stage = new Stage();
        stage.setTitle("Simulation Results - ID " + simulationId);

        TableView<ResultsTable> table = ResultsTable.buildTable();

        List<SimulationResult> results = DatabaseManager.getResultsBySimulationId(simulationId);

        // Convertim SimulationResult in ResultsTable
        for (SimulationResult r : results) {
            table.getItems().add(new ResultsTable(
                    r.getCloudletId(),
                    r.getStatus(),
                    r.getVmId(),
                    r.getHostId(),
                    String.valueOf(r.getStartTime()),
                    String.valueOf(r.getFinishTime()),
                    String.valueOf(r.getExecTime()),
                    String.valueOf(r.getEnergy())
            ));
        }

        VBox layout = new VBox(10, table);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #e0eaf5;");

        Scene scene = new Scene(layout, 1000, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static TableView<ResultsTable> buildTable() {
        TableView<ResultsTable> table = new TableView<>();

        TableColumn<ResultsTable, String> idCol = new TableColumn<>("CloudletID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCloudletId()));

        TableColumn<ResultsTable, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<ResultsTable, String> vmCol = new TableColumn<>("VM");
        vmCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVm()));

        TableColumn<ResultsTable, String> hostCol = new TableColumn<>("Host");
        hostCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHost()));

        TableColumn<ResultsTable, String> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartTime()));

        TableColumn<ResultsTable, String> finishCol = new TableColumn<>("Finish Time");
        finishCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFinishTime()));

        TableColumn<ResultsTable, String> execCol = new TableColumn<>("Exec Time");
        execCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExecTime()));

        TableColumn<ResultsTable, String> energyCol = new TableColumn<>("Energy");
        energyCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEnergy()));

        table.getColumns().addAll(idCol, statusCol, vmCol, hostCol, startCol, finishCol, execCol, energyCol);
        return table;
    }
}