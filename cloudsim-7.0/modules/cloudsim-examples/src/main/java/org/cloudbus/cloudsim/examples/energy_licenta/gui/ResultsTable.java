package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

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