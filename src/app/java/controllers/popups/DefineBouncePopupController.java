package app.java.controllers.popups;

import app.java.controllers.GraphController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DefineBouncePopupController {

    GraphController graphController;

    @FXML
    public RadioButton pagesRadioButton;
    @FXML
    public Label pagesLabel;
    @FXML
    public TextField pagesTextField;
    @FXML
    public RadioButton exitRadioButton;
    @FXML
    public Label exitLabel;
    @FXML
    public TextField exitTextField;
    @FXML
    public Button closeButton;

    private Button defineBounceButton;
    private Button bouncesButton;
    private Button bounceRateButton;

    public void initializeController()
    {
        boolean hasBeenInitialized = graphController.getWasInitialized();

        if(hasBeenInitialized)
        {
            boolean usingPages = graphController.getUsingPagesBoolean();
            int value = graphController.getCustomValue();

            if(usingPages)
            {
                pagesRadioButton.setSelected(true);
                pagesLabel.setDisable(false);
                pagesTextField.setDisable(false);
                pagesTextField.setText(String.valueOf(value));
            }
            else
            {
                exitRadioButton.setSelected(true);
                exitLabel.setDisable(false);
                exitTextField.setDisable(false);
                exitTextField.setText(String.valueOf(value));
            }
        }

    }

    public void cancel(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void confirm() {
        String input;
        boolean usingPages;
        if(!pagesTextField.isDisabled())
        {
            input = pagesTextField.getText();
            usingPages = true;
        }
        else
        {
            input = exitTextField.getText();
            usingPages = false;
        }

        if(!input.matches("[1-9][0-9]*"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong input");
            alert.setContentText("Please input a positive, non zero number");
            alert.showAndWait();
            return;
        }

        int value;
        String buttonText;
        if(usingPages)
        {
            value = Integer.parseInt(pagesTextField.getText());
            buttonText = "Bounce Definition - Pages Viewed: " + pagesTextField.getText();
        }
        else
        {
            value = Integer.parseInt(exitTextField.getText());
            buttonText = "Bounce Definition - Exit Time: " + exitTextField.getText() + "s";
        }


        if(bouncesButton.isDisabled())
        {
            bouncesButton.setDisable(false);
            bounceRateButton.setDisable(false);
        }

        defineBounceButton.setText(buttonText);

        graphController.setWasInitialized();
        graphController.setCustomBounce(usingPages, value);

        try {
            String selectedSQL = graphController.currentSQLQuery_Total_Name;
            if(selectedSQL.equals("Bounces") || selectedSQL.equals("Bounce Rate"))
            {
                graphController.createLineFromMetric(selectedSQL);
            }
        }
        catch(Exception e)
        {
            //ignored
        }


        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void pagesClick(ActionEvent actionEvent) {
        pagesLabel.setDisable(false);
        pagesTextField.setDisable(false);

        exitLabel.setDisable(true);
        exitTextField.setDisable(true);
        exitTextField.setText("");
    }

    public void exitClick(ActionEvent actionEvent) {
        exitLabel.setDisable(false);
        exitTextField.setDisable(false);

        pagesLabel.setDisable(true);
        pagesTextField.setDisable(true);
        pagesTextField.setText("");
    }

    public void setButtons(Button defineBounceButton, Button bouncesButton, Button bounceRateButton)
    {
        this.defineBounceButton = defineBounceButton;
        this.bouncesButton = bouncesButton;
        this.bounceRateButton = bounceRateButton;
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }

    public boolean isStringValid(String str){
        return str.matches("[1-9][0-9]*");
    }
}
