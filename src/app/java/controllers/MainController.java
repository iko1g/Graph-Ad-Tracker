package app.java.controllers;

import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for Main.
 */
public class MainController {

    @FXML
    private GraphController graphController;

    @FXML
    private MetricController metricsController;


    public void initialize() throws IOException, SQLException {
        this.metricsController.setGraphController(this.graphController);
        this.graphController.setMetricController(this.metricsController);
    }
}