package app.java.controllers.popups;

import app.java.controllers.MetricController;
import app.java.util.Database;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

public class ImportPopupController {

    @FXML
    public ProgressBar progressBar;
    @FXML
    private javafx.scene.control.Button confirmButton;
    @FXML
    private javafx.scene.control.Button closeButton;
    @FXML
    private javafx.scene.control.Button impressionButton;
    @FXML
    private javafx.scene.control.Button clicksButton;
    @FXML
    private javafx.scene.control.Button serversButton;

    private File selectedImpressions;
    private File selectedClicks;
    private File selectedServers;

    private boolean impressionsFileChosen = false;
    private boolean clicksFileChosen = false;
    private boolean serversFileChosen = false;

    Connection connection;

    private MetricController metricController;

    public void setMetricController(MetricController metricController) {
        this.metricController = metricController;
    }

    /**
     * Closes the popup Window.
     */
    @FXML
    public void close () {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Closes the popup Window.
     * @throws SQLException TODO: define here
     * @throws IOException which is used by the bufferedReader
     */
    @FXML
    public void confirm() throws SQLException, IOException {

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                connection = Database.getConnection();

                //We ensure the DB has the selected table
                Database.cleanMainTable();

                PreparedStatement mainStatement = connection.prepareStatement("INSERT INTO mainTable VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?)");

                BufferedReader impressionReader = null;
                BufferedReader clickReader = null;
                BufferedReader serverReader = null;

                try {
                    impressionReader = new BufferedReader(new FileReader(selectedImpressions));
                    clickReader = new BufferedReader(new FileReader(selectedClicks));
                    serverReader = new BufferedReader(new FileReader(selectedServers));
                } catch (FileNotFoundException exception) {
                    System.err.println("Did not provide data logs");
                }

                assert impressionReader != null;
                assert clickReader != null;
                assert serverReader != null;

                String impressionNextLine = impressionReader.readLine();
                String clickNextLine = clickReader.readLine();
                String serverNextLine = serverReader.readLine();
                String[] categories = impressionNextLine.split(",");

                //If the log does not have the below structure
                if (!(categories[0].equals("Date") && categories[1].equals("ID") && categories[2].equals("Gender") &&
                        categories[3].equals("Age") && categories[4].equals("Income") &&
                        categories[5].equals("Context") && categories[6].equals("Impression Cost"))) {
                    Platform.runLater(() -> {
                        impressionButton.setStyle("-fx-background-color: red");
                        impressionButton.setText("Impression logs: Insertion Error");
                        impressionsFileChosen = false;
                    });
                }

                updateProgress(33, 100);
                Thread.sleep(10);

                categories = clickNextLine.split(",");
                //If the log does not have the below structure
                if (!(categories[0].equals("Date") && categories[1].equals("ID") && categories[2].equals("Click Cost"))) {
                    Platform.runLater(() -> {
                        clicksButton.setStyle("-fx-background-color: red");
                        clicksButton.setText("Click logs: Insertion Error");
                        clicksFileChosen = false;
                    });
                }

                categories = serverNextLine.split(",");
                System.out.println(Arrays.toString(categories));
                //If the log does not have the below structure
                if (!(categories[0].equals("Entry Date") && categories[1].equals("ID") && categories[2].equals("Exit Date") &&
                        categories[3].equals("Pages Viewed") && categories[4].equals("Conversion"))) {
                    Platform.runLater(() -> {
                        serversButton.setStyle("-fx-background-color: red");
                        serversButton.setText("Server logs: Insertion Error");
                        serversFileChosen = false;
                    });
                }

                int clickCounter = 0; //Debugging purposes

                while ((clickNextLine = clickReader.readLine()) != null && (serverNextLine = serverReader.readLine()) != null) {
                    clickCounter++; //Debugging purposes
                    String[] clickArray = clickNextLine.split(",");
                    String[] serverArray = serverNextLine.split(",");

                    if(!clickArray[1].equals(serverArray[1]))
                    {
                        Platform.runLater(() -> {
                            serversButton.setStyle("-fx-background-color: red");
                            serversButton.setText("Server logs: Insertion Error");
                            clicksButton.setStyle("-fx-background-color: red");
                            clicksButton.setText("Click logs: Insertion Error");
                            serversFileChosen = false;
                            clicksFileChosen = false;
                        });
                    }

                    while ((impressionNextLine = impressionReader.readLine()) != null)
                    {
                        String[] impressionArray = impressionNextLine.split(",");

                        if(impressionArray[1].equals(clickArray[1]))
                        {
                            createMainStatement(mainStatement, impressionArray);
                            //mainStatement.setString(1, String.valueOf(arrOfValues[0])); //ClickDate
                            mainStatement.setFloat(8, Float.parseFloat(clickArray[2])); //ClickCost
                            mainStatement.setString(9, String.valueOf(serverArray[0])); //EntryDate
                            mainStatement.setString(10, String.valueOf(serverArray[2])); //ExitDate
                            mainStatement.setString(11, String.valueOf(serverArray[3])); //PagesViewed
                            mainStatement.setString(12, String.valueOf(serverArray[4])); //Conversion
                            mainStatement.addBatch();

                            break; //Goes out of inner loop into outer loop
                        }
                        else
                        {
                            createMainStatement(mainStatement, impressionArray);
                        }
                    }
                }

                updateProgress(66, 100);
                Thread.sleep(10);

                //Finishing up last few entries
                while ((impressionNextLine = impressionReader.readLine()) != null) {
                    String[] impressionArray = impressionNextLine.split(",");
                    {
                        createMainStatement(mainStatement, impressionArray);
                    }
                }

                mainStatement.executeBatch();
                System.out.println("Done inserting into main table");
                connection.commit();
                confirmButton.setDisable(true);
                updateProgress(100, 100);
                Thread.sleep(10);

                Platform.runLater(() -> {
                    closeButton.setStyle("-fx-background-color: #15d415");
                    closeButton.setText("Done. Please close.");
                });

                metricController.enableAllButtons();

                return null;
            }
        };

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void createMainStatement(PreparedStatement mainStatement, String[] impressionArray) throws SQLException {
        mainStatement.setString(1, String.valueOf(impressionArray[0])); //Date
        mainStatement.setLong(2, Long.parseLong(impressionArray[1])); //ID
        mainStatement.setString(3, String.valueOf(impressionArray[2])); //Gender
        mainStatement.setString(4, String.valueOf(impressionArray[3])); //Age
        mainStatement.setString(5, String.valueOf(impressionArray[4])); //Income
        mainStatement.setString(6, String.valueOf(impressionArray[5])); //Context
        mainStatement.setString(7, String.valueOf(impressionArray[6])); //ImpressionCost
        mainStatement.setNull(8, Types.NULL); //ClickCost
        mainStatement.setNull(9, Types.NULL); //EntryDate
        mainStatement.setNull(10, Types.NULL); //ExitDate
        mainStatement.setNull(11, Types.NULL); //PagesViewed
        mainStatement.setNull(12, Types.NULL); //Conversion
        mainStatement.addBatch();
    }

    /**
     * Gets the impressions sql file
     */
    public void impressionsInsertion() {
        selectedImpressions = null;
        impressionsFileChosen = false;
        Stage stage = (Stage) impressionButton.getScene().getWindow();

        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        filechooser.setTitle("Open Impressions CSV File");
        selectedImpressions = filechooser.showOpenDialog(stage);

        if (selectedImpressions != null) {
            impressionButton.setStyle("-fx-border-color: black");
            impressionButton.setText("Impression Log: " + selectedImpressions.getName());
            impressionsFileChosen = true;
            unlockConfirmButton();
        }
        else {
            impressionsFileChosen = false;
            resetButton(impressionButton);
        }
    }

    /**
     * Gets the clicks sql file
     */
    public void clicksInsertion() {
        Stage stage = (Stage) clicksButton.getScene().getWindow();

        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        filechooser.setTitle("Open Clicks CSV File");
        selectedClicks = filechooser.showOpenDialog(stage);

        if (selectedClicks != null) {
            clicksButton.setStyle("-fx-border-color: black");
            clicksButton.setText("Click Log: " + selectedClicks.getName());
            clicksFileChosen = true;
            unlockConfirmButton();
        }
        else {
            clicksFileChosen = false;
            resetButton(clicksButton);
        }
    }

    /**
     * Gets the servers sql file
     */
    public void serversInsertion() {
        Stage stage = (Stage) serversButton.getScene().getWindow();

        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        filechooser.setTitle("Open Servers CSV File");
        selectedServers = filechooser.showOpenDialog(stage);

        if (selectedServers != null) {
            serversButton.setStyle("-fx-border-color: black");
            serversButton.setText("Server Log: " + selectedServers.getName());
            serversFileChosen = true;
            unlockConfirmButton();
        }
        else {
            serversFileChosen = false;
            resetButton(serversButton);
        }
    }

    /**
     * Enables the confirm button after all the files have been chosen.
     */
    public void unlockConfirmButton () {
        if (impressionsFileChosen && clicksFileChosen && serversFileChosen) {
            confirmButton.setDisable(false);
        }
    }

    public void resetButton (Button button) {
        String[] words = button.getText().split(" ");
        button.setText(words[0] + " " + words[1]);
        button.setStyle("-fx-border-color: red");
        confirmButton.setDisable(true);
    }
}
