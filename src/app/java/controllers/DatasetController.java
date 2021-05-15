package app.java.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the the popup.
 * Contains a method which shows the popup.
 */
public class DatasetController {
    /**
     * Opens a popup to add the data from a database
     * @param e the click event of the button
     * @throws IOException from the databaseTemp
     */
    @FXML
    public void showAddDataPopup (Event e) throws IOException {
        Stage popupWindow = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/app/resources/fxml/popups/ImportPopup.fxml"));
        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setScene(new Scene(root, 400, 400));
        popupWindow.show();
    }

//    public void enterSettingsPopup(MouseEvent mouseEvent) throws IOException {
//        Stage popupWindow = new Stage();
//        Parent root = FXMLLoader.load(getClass().getResource("/app/resources/fxml/popups/SettingsPopup.fxml"));
//        popupWindow.initModality(Modality.APPLICATION_MODAL);
//        popupWindow.setScene(new Scene(root, 400, 400));
//        popupWindow.show();
//    }
}
