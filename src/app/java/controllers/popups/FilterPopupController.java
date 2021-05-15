package app.java.controllers.popups;

import app.java.controllers.GraphController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Controller class for the filter popup.
 * Contains method to show the popup and to choose a filter.
 */
public class FilterPopupController {

    private GraphController graphController;

    public Button closeButton;
    @FXML
    private javafx.scene.control.MenuButton ageFilterButton;
    @FXML
    private javafx.scene.control.MenuButton genderFilterButton;
    @FXML
    private javafx.scene.control.MenuButton incomeFilterButton;
    @FXML
    private javafx.scene.control.MenuButton contextFilterButton;

    @FXML
    private javafx.scene.control.Button confirmButton;

    @FXML
    private DatePicker fromDateInFilter;
    @FXML
    private DatePicker toDateInFilter;

    String[] SQLFilterQueries = new String[]{"", "", "", "",""};

    String[] filterAppliedLabel = new String[]{"", "", "", "",""};

    public boolean deleteLastLines;

    String button;
    
    
    public FilterPopupController(boolean deleteLastLines) {
        this.deleteLastLines = deleteLastLines;
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
        this.fromDateInFilter = new DatePicker();

    }

    /**
     * function for when user clicks confirm button -> joins all sql query filters into one string and creates new line
     */
    @FXML
    public void confirm() {

        String impressionsQuery = this.graphController.currentSQLQuery;
        String joinedLabels = String.join(",",filterAppliedLabel).replace(",","");

        String joinedSQLfilters = String.join(",",SQLFilterQueries).replace(",","");
        String fromdateString = fromDateInFilter.getEditor().getText();

        String todateString = toDateInFilter.getEditor().getText();
        if (isDateValid(fromdateString) && isDateValid(todateString)){
            if (joinedSQLfilters.length() > 0){

                joinedSQLfilters = joinedSQLfilters.substring(4);



                impressionsQuery = impressionsQuery.replaceAll("WHERE", "WHERE " + (joinedSQLfilters+ " AND "));
                impressionsQuery = impressionsQuery.replaceAll("<f>", "WHERE " + (joinedSQLfilters+ " "));

                String fromDate = GraphController.reverseDateOrder(fromDateInFilter.getEditor().getText());
                String toDate = GraphController.reverseDateOrder(toDateInFilter.getEditor().getText());

                String dateFilterString = " date(Date) BETWEEN " + "'" + fromDate + "'" + " AND " + "'" + toDate + "'";


                impressionsQuery = graphController.replaceDateInSQL(impressionsQuery, dateFilterString);

                //this.graphController.updateMetricTotal(this.graphController.calcMetricTotal(this.graphController.convertToTotal(impressionsQuery)));
                String numberTotal = (this.graphController.calcMetricTotal(this.graphController.convertToTotal(impressionsQuery)));

                joinedLabels = joinedLabels + "| Total = " + numberTotal ;

                this.graphController.createLine(impressionsQuery, joinedLabels, deleteLastLines);
            }
            else {
                String fromDate = GraphController.reverseDateOrder(fromDateInFilter.getEditor().getText());
                String toDate = GraphController.reverseDateOrder(toDateInFilter.getEditor().getText());



                String dateFilterString = " date(Date) BETWEEN " + "'" + fromDate + "'" + " AND " + "'" + toDate + "'";

                impressionsQuery = impressionsQuery.replaceAll("WHERE", "WHERE " + (dateFilterString+ " AND "));
                impressionsQuery = impressionsQuery.replaceAll("<f>", "WHERE " + (dateFilterString+ " "));

                //impressionsQuery = graphController.replaceDateInSQL(impressionsQuery, dateFilterString);

                String numberTotal = (this.graphController.calcMetricTotal(this.graphController.convertToTotal(impressionsQuery)));

                String noFilters = graphController.currentSQLQuery_Total_Name + " | Total = " + numberTotal ;

                this.graphController.createLine(impressionsQuery, noFilters, deleteLastLines);

            }
        }


        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }


    public boolean isDateValid(String date)
    {

        if (date.length() != 10){
            return false;
        }
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Age filter for under 25 years old.
     */
    @FXML
    public void young() {
        Array.set(filterAppliedLabel,0,"<25 ");
        Array.set(SQLFilterQueries,0," AND Age = '<25'");
        //addToQuery("AND age < 25", " <25");
        ageFilterButton.setText("Age: <25");
    }

    /**
     * Age filter for between 25 and 34 years old.
     */
    @FXML
    public void between_25_34() {
        Array.set(filterAppliedLabel,0,"25 to 34 ");
        Array.set(SQLFilterQueries,0," AND Age = '25-34'");

        //addToQuery("AND age BETWEEN 25 and 34", " 25-34");
        ageFilterButton.setText("Age: 25-34");
    }

    /**
     * Age filter for between 35 and 44 years old.
     */
    @FXML
    public void between_35_44() {
        Array.set(filterAppliedLabel,0,"35 to 44 ");
        Array.set(SQLFilterQueries,0," AND Age = '35-44'");

        //addToQuery("AND age BETWEEN 35 and 44", " 35-44");
        ageFilterButton.setText("Age: 35-44");
    }

    /**
     * Age filter for between 45 and 54 years old.
     */
    @FXML
    public void between_45_54() {
        Array.set(filterAppliedLabel,0,"45 to 54 ");
        Array.set(SQLFilterQueries,0," AND Age = '45-54'");

        //addToQuery("AND age BETWEEN 45 and 54", " 45-54");
        ageFilterButton.setText("Age: 45-54");
    }

    /**
     * Age filter for over 54 years old.
     */
    @FXML
    public void old() {
        Array.set(filterAppliedLabel,0,">54 ");
        Array.set(SQLFilterQueries,0," AND Age = '>54'");

        //addToQuery("AND age > 54", " >54");
        ageFilterButton.setText("Age: >54");
    }

    /**
     * Gender filters.
     * @param e which is the menu for said filter.
     */
    @FXML
    public void genderFilter (Event e) {
        MenuItem clickedItem = (MenuItem)e.getSource();
        String gender = clickedItem.getText();
        Array.set(filterAppliedLabel,1,gender + " ");
        Array.set(SQLFilterQueries,1," AND Gender = '"+ gender + "'");


        //addToQuery("AND gender = '"+ gender + "'", " " + gender);
        genderFilterButton.setText("Gender: " + gender);

    }

    /**
     * Income Filter
     * @param e which is the menu of said filter
     */
    @FXML
    public void incomeFilter (Event e) {
        MenuItem clickedItem = (MenuItem)e.getSource();
        String income = clickedItem.getText();
        Array.set(filterAppliedLabel,2,income + " ");
        Array.set(SQLFilterQueries,2," AND Income = '"+ income + "'");

        //addToQuery("AND income = '"+ income + "'", " " + income);
        incomeFilterButton.setText("Income: " + income);
    }

    /**
     * Context filter
     * Note that in the spec it says travel is a context yet its not in the impression log
     * @param e which is the menu for said filter
     */
    @FXML
    public void contextFilter (Event e) {
        MenuItem clickedItem = (MenuItem)e.getSource();
        String context = clickedItem.getText();
        Array.set(filterAppliedLabel,3,context + " ");
        Array.set(SQLFilterQueries,3," AND Context = '"+ context + "'");

        //addToQuery("AND context = '"+ context + "'", " " + context);
        contextFilterButton.setText("Context: " + context);

    }

    /**
     * Cancel button which closes the popup.
     */
    @FXML
    public void cancel() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}