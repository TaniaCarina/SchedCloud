package org.cloudbus.cloudsim.examples.energy_licenta.gui.layout;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cloudbus.cloudsim.examples.energy_licenta.gui.components.ResultsTable;

public class MainLayoutBuilder {

    public static class LayoutComponents {
        public VBox leftPane;
        public VBox rightPane;
        public Label summaryLabel;
        public TextField hostsInput, hostMIPSInput, hostRAMInput, hostCoresInput;
        public TextField vmsInput, vmMIPSInput, vmRAMInput, vmBWInput, vmSizeInput, pesNumberInput, cloudletsInput;
        public ComboBox<String> algoSelect;
        public RadioButton normalSimButton, dynamicSimButton;
        public Button runButton, suggestButton, saveButton, loadButton;
        public TableView<ResultsTable> resultsTable;

    }

    public static Parent build(LayoutComponents components) {
        // Style strings
        String labelStyle = "-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;";
        String inputStyle = "-fx-background-color: #ffffff; -fx-text-fill: #0D1B2A;";

        // === LEFT PANE ===
        VBox leftPane = new VBox(12);
        leftPane.setPadding(new Insets(20));
        leftPane.setPrefWidth(440);

        components.hostsInput = styledField("10", inputStyle);
        components.hostMIPSInput = styledField("10000", inputStyle);
        components.hostRAMInput = styledField("32", inputStyle);
        components.hostCoresInput = styledField("8", inputStyle);
        components.vmsInput = styledField("40", inputStyle);
        components.vmMIPSInput = styledField("2500", inputStyle);
        components.vmRAMInput = styledField("2", inputStyle);
        components.vmBWInput = styledField("1000", inputStyle);
        components.vmSizeInput = styledField("10000", inputStyle);
        components.pesNumberInput = styledField("1", inputStyle);
        components.cloudletsInput = styledField("50", inputStyle);

        components.algoSelect = new ComboBox<>();
        components.algoSelect.getItems().addAll("MinLengthRoundRobin", "RoundRobin", "ACO", "FCFS", "Random", "LJF", "MinMin", "MaxMin", "PSO", "Genetic");
        components.algoSelect.setValue("MinLengthRoundRobin");

        Label modeLabel = new Label("Select Simulation Mode:");
        modeLabel.setStyle(labelStyle);
        components.normalSimButton = new RadioButton("Normal");
        components.dynamicSimButton = new RadioButton("Energy Efficient");
        ToggleGroup toggleGroup = new ToggleGroup();
        components.normalSimButton.setToggleGroup(toggleGroup);
        components.dynamicSimButton.setToggleGroup(toggleGroup);
        components.dynamicSimButton.setSelected(true);

        components.runButton = styledButton("Run Simulation", "#5193c7");
        components.suggestButton = styledButton("Suggest Resources", "#6c99c3");
        components.saveButton = styledButton("Save Results", "#6c99c3");
        components.loadButton = styledButton("Load Results", "#6c99c3");

        leftPane.getChildren().addAll(
                modeLabel, components.normalSimButton, components.dynamicSimButton,
                labeledBox("Number of Hosts:", components.hostsInput, labelStyle),
                labeledBox("Host MIPS:", components.hostMIPSInput, labelStyle),
                labeledBox("Host RAM (GB):", components.hostRAMInput, labelStyle),
                labeledBox("Cores per Host:", components.hostCoresInput, labelStyle),
                labeledBox("Number of VMs:", components.vmsInput, labelStyle),
                labeledBox("VM MIPS:", components.vmMIPSInput, labelStyle),
                labeledBox("VM RAM (GB):", components.vmRAMInput, labelStyle),
                labeledBox("VM Bandwidth (MB/s):", components.vmBWInput, labelStyle),
                labeledBox("VM Storage (MB):", components.vmSizeInput, labelStyle),
                labeledBox("Cores per VM:", components.pesNumberInput, labelStyle),
                labeledBox("Number of Cloudlets (Tasks):", components.cloudletsInput, labelStyle),
                labeledBox("Scheduling Algorithm:", components.algoSelect, labelStyle),
                new HBox(10, components.runButton),
                new HBox(10, components.suggestButton, components.saveButton, components.loadButton)
        );

        // === RIGHT PANE ===
        VBox rightPane = new VBox(12);
        rightPane.setPadding(new Insets(20));
        rightPane.setPrefWidth(600);

        Label summaryTitle = new Label("Summary:");
        summaryTitle.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");

        components.resultsTable = ResultsTable.buildTable();
        components.resultsTable.setPrefHeight(400);
        components.resultsTable.setMinWidth(600);
        components.resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        components.summaryLabel = new Label("Total Energy: 0 \nAlgorithm: ");
        components.summaryLabel.setStyle("-fx-text-fill: #0D1B2A; -fx-font-size: 14px; -fx-font-weight: bold;");

        rightPane.getChildren().addAll(summaryTitle, components.resultsTable, components.summaryLabel);

        // Combine in main layout
        components.leftPane = leftPane;
        components.rightPane = rightPane;

        HBox layout = new HBox(leftPane, rightPane);
        layout.setSpacing(20);
        layout.setStyle("-fx-background-color: #e0eaf5;");
        return layout;
    }

    private static TextField styledField(String text, String style) {
        TextField field = new TextField(text);
        field.setStyle(style);
        return field;
    }

    private static Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(150);
        btn.setPrefHeight(50);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius:8; -fx-cursor: hand;");
        return btn;
    }

    private static HBox labeledBox(String labelText, Control control, String style) {
        Label label = new Label(labelText);
        label.setStyle(style);
        return new HBox(10, label, control);
    }


}
