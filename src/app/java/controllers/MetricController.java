package app.java.controllers;

import app.java.controllers.popups.DefineBouncePopupController;
import app.java.controllers.popups.ImportPopupController;
import app.java.util.Database;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MetricController {

    public Button impressionsButton;
    public Button clicksMetricButton;
    public Button uniquesButton;
    public Button conversionsButton;
    public Button totalCostButton;
    public Button CTRButton;
    public Button CPAButton;
    public Button CPMButton;
    public Button CPCButton;
//    public Button bounceRateButton;
//    public Button bouncesButton;
    public Button defineBounceButton;

    private ImportPopupController importPopupController;

    private GraphController graphController;

    private Connection connection;

    public MetricController() {
        this.connection = Database.getConnection();

        this.importPopupController = new ImportPopupController();
    }

    public void setGraphController(GraphController graphController) throws SQLException {
        this.graphController = graphController;

        if (!Database.checkIfDataExists()) {
            this.disableAllButtons();
        }
    }

    Button selectedButton = null;
    String selectedBounce = "One Page Viewed";

    @FXML
    ChoiceBox defineBounce;

//    @FXML
//    Button defineBounceButton;
    @FXML
    Button bouncesButton;
    @FXML
    Button bounceRateButton;

    public void initialize() throws SQLException {
        this.importPopupController.setMetricController(this);
    }

    private void disableAllButtons() {
        this.impressionsButton.setDisable(true);
        this.clicksMetricButton.setDisable(true);
        this.uniquesButton.setDisable(true);
        this.conversionsButton.setDisable(true);
        this.totalCostButton.setDisable(true);
        this.CTRButton.setDisable(true);
        this.CPAButton.setDisable(true);
        this.CPMButton.setDisable(true);
        this.CPCButton.setDisable(true);
        this.defineBounceButton.setDisable(true);
        this.graphController.getHistogramButton().setDisable(true);
    }

    public void enableAllButtons() {
        this.impressionsButton.setDisable(false);
        this.clicksMetricButton.setDisable(false);
        this.uniquesButton.setDisable(false);
        this.conversionsButton.setDisable(false);
        this.totalCostButton.setDisable(false);
        this.CTRButton.setDisable(false);
        this.CPAButton.setDisable(false);
        this.CPMButton.setDisable(false);
        this.CPCButton.setDisable(false);
        this.defineBounceButton.setDisable(false);
        this.graphController.getHistogramButton().setDisable(false);
    }

    @FXML
    public void metricButtonClick (Event e) {
        graphController.visuallyDeselectClickCostButton();

        Button b = (Button)e.getSource();

        if (selectedButton != null) {
            visuallyDeselectButton();
        }

        selectedButton = b;

        visuallySelectButton();

        this.graphController.createLineChart(b.getText());

//        if (b.getText().equals("Bounce Rate")) {
//            this.graphController.createLineChart("Custom");
//        } else {
//            this.graphController.createLineChart(b.getText());
//        }
    }

    /**
     * Selects the bounce rate.
     * @param e which is the click event.
     */
    public void selectBounce(ActionEvent e) {
        ChoiceBox choiceBox = (ChoiceBox) e.getSource();
        Object selectedItem = choiceBox.getSelectionModel().getSelectedItem();
        defineBounce.setValue("Bounce Rate: " + selectedItem);
        selectedBounce = (String) selectedItem;
    }

    public void openDefineBounce(ActionEvent mouseEvent) throws IOException {
        DefineBouncePopupController controller = new DefineBouncePopupController();
        controller.setGraphController(this.graphController);
        controller.setButtons(defineBounceButton, bouncesButton, bounceRateButton);

        Stage popupWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/resources/fxml/popups/DefineBouncePopup.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        controller.initializeController();

        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setScene(new Scene(root));
        popupWindow.show();
    }

    public void visuallySelectButton() {
        selectedButton.setStyle("-fx-background-color: cyan;");
    }

    public void visuallyDeselectButton() {
        if (this.selectedButton != null) {
            selectedButton.setStyle("-fx-background-color: transparent");
        }
    }

    public void showAddDataPopup (Event e) throws IOException {

        Stage popupWindow = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/resources/fxml/popups/ImportPopup.fxml"));
        loader.setController(this.importPopupController);
        Parent root = loader.load();

        popupWindow.setTitle("Ad Auction Software");
        popupWindow.setScene(new Scene(root, 400, 400));
        popupWindow.show();
    }
}
