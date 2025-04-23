package com.example.maged;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;

public class DashboardController {

    @FXML
    private PieChart pieChart;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    public void initialize() {
        // Pie Chart Data
        PieChart.Data slice1 = new PieChart.Data("Checking", 3000);
        PieChart.Data slice2 = new PieChart.Data("Savings", 5000);
        PieChart.Data slice3 = new PieChart.Data("Wallet", 1000);
        pieChart.getData().addAll(slice1, slice2, slice3);

        // Line Chart Data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Account Balance Over Time");
        series.getData().add(new XYChart.Data<>("Jan", 3000));
        series.getData().add(new XYChart.Data<>("Feb", 4000));
        series.getData().add(new XYChart.Data<>("Mar", 3500));
        series.getData().add(new XYChart.Data<>("Apr", 5000));
        lineChart.getData().add(series);

        // Bar Chart Data
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Account Comparison");
        barSeries.getData().add(new XYChart.Data<>("Checking", 3000));
        barSeries.getData().add(new XYChart.Data<>("Savings", 5000));
        barSeries.getData().add(new XYChart.Data<>("Wallet", 1000));
        barChart.getData().add(barSeries);
    }
}
