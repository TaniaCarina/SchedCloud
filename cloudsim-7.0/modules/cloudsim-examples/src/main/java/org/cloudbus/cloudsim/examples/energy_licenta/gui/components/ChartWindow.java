package org.cloudbus.cloudsim.examples.energy_licenta.gui.components;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.cloudbus.cloudsim.examples.energy_licenta.db.DatabaseManager;
import org.cloudbus.cloudsim.examples.energy_licenta.db.SimulationResult;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChartWindow {

    public static BarChart<String, Number> createEnergyChartFromDb(String simulationId) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("CloudletID");
        yAxis.setLabel("Energy (kJ)");

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

    public static void showEnergyChartWindow(
            BarChart<String, Number> chart,
            String algorithm,
            boolean isDynamic,
            int numHosts,
            int numVMs,
            int numCloudlets,
            double totalEnergy,
            double realExecTime,
            double totalExecTime
    ) {
        chart.setStyle("-fx-bar-fill: #ec6ba1;");
        chart.setPrefSize(850, 500);

        Label chartTitle = new Label("Energy Consumption Chart:");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0D1B2A;");

        Label leftInfo = new Label(
                "Algorithm: " + algorithm +
                        "\nEnergy Efficient: " + (isDynamic ? "Enabled" : "Disabled") +
                        "\nTotal Energy: " + String.format("%.2f", totalEnergy) + " kJ" +
                        "\nExecution Time (real): " + String.format("%.2f", realExecTime) + " sec"
        );
        leftInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #0D1B2A; -fx-font-weight: bold;");

        Label rightInfo = new Label(
                "Number of Hosts: " + numHosts +
                        "\nNumber of VMs: " + numVMs +
                        "\nNumber of Cloudlets: " + numCloudlets +
                        "\nCloudlets Exec Time: " + String.format("%.2f", totalExecTime) + " sec"
        );
        rightInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #0D1B2A; -fx-font-weight: bold;");

        HBox infoBox = new HBox(150);
        infoBox.getChildren().addAll(leftInfo, rightInfo);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-alignment: center;");

        VBox layout = new VBox(20, chartTitle, chart, infoBox);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #e0eaf5;");

        Scene scene = new Scene(layout, 1020, 800);
        Stage chartStage = new Stage();
        chartStage.setTitle("Energy Chart");
        chartStage.setScene(scene);
        chartStage.show();

        // Salveaza graficul ca PNG dupa 0.5 secunde pentru a se genera corect imaginea
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(event -> {
            javafx.scene.image.WritableImage image = layout.snapshot(new SnapshotParameters(), null);
            File file = new File(String.format("chart_%s_%dVM_%dCL_%s.png",
                    (isDynamic ? "DS" : "NDS"),
                    numVMs,
                    numCloudlets,
                    algorithm.replaceAll("\\s+", "")));
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                System.out.println("Chart saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        delay.play();
    }

    public static BarChart<String, Number> createEnergyChartFromTable(TableView<ResultsTable> table) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("CloudletID");
        yAxis.setLabel("Energy (kJ)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Energy Consumption per Cloudlet");
        chart.setPrefHeight(250);
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (ResultsTable row : table.getItems()) {
            try {
                double energy = Double.parseDouble(row.getEnergy());
                series.getData().add(new XYChart.Data<>(row.getCloudletId(), energy));
            } catch (NumberFormatException ignored) {
            }
        }

        chart.getData().add(series);
        return chart;
    }

}
