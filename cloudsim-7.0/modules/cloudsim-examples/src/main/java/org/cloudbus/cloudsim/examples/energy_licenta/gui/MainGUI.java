package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import javafx.application.Application;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;

import javafx.stage.Stage;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SchemaInitializer;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.components.ResultsTable;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.handlers.ButtonActionHandler;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.layout.MainLayoutBuilder;


public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CloudSim7G - Energy Simulation");

        // Initializeaza tabelele DB daca nu exista
        SchemaInitializer.createTableIfNotExists();
        SchemaInitializer.createSummaryTableIfNotExists();

        // Construieste layout-ul si componentele UI
        MainLayoutBuilder.LayoutComponents components = new MainLayoutBuilder.LayoutComponents();
        Parent mainLayout = MainLayoutBuilder.build(components);

        // Creeaza obiectul pentru starea ultimei simulari
        ButtonActionHandler.SimulationState simState = new ButtonActionHandler.SimulationState();

        // Seteaza actiunile butoanelor folosind ButtonActionHandler
        components.runButton.setOnAction(e -> ButtonActionHandler.handleRunSimulation(
                simState,
                components.dynamicSimButton.isSelected(),
                Integer.parseInt(components.hostsInput.getText()),
                Integer.parseInt(components.hostMIPSInput.getText()),
                Integer.parseInt(components.hostRAMInput.getText()) * 1024,
                Integer.parseInt(components.vmsInput.getText()),
                Integer.parseInt(components.vmMIPSInput.getText()),
                Integer.parseInt(components.vmRAMInput.getText()) * 1024,
                Long.parseLong(components.vmBWInput.getText()),
                Long.parseLong(components.vmSizeInput.getText()),
                Integer.parseInt(components.pesNumberInput.getText()),
                Integer.parseInt(components.cloudletsInput.getText()),
                components.algoSelect.getValue(),
                (TableView<ResultsTable>) components.resultsTable,
                components.summaryLabel
        ));

        components.suggestButton.setOnAction(e -> ButtonActionHandler.handleSuggestResources(
                components.vmsInput,
                components.vmRAMInput,
                components.vmMIPSInput,
                components.pesNumberInput,
                components.hostRAMInput,
                components.hostMIPSInput,
                components.hostCoresInput,
                components.hostsInput
        ));

        components.saveButton.setOnAction(e -> ButtonActionHandler.handleSaveSimulation(simState, (TableView<ResultsTable>) components.resultsTable));

        components.loadButton.setOnAction(e -> ButtonActionHandler.handleLoadSimulation());

        Scene scene = new Scene(mainLayout, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}