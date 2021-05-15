package app.java.controllers.popups;

import app.java.controllers.GraphController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Controller class for the filter popup.
 * Contains method to show the popup and to choose a filter.
 */
public class DateRangePopupController {

    private GraphController graphController;

    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;

    private String currentFromDate;
    private String currentToDate;

    private ArrayList<String> queries;
    private LineChart<String,Number> lineChart;

    @FXML
    private javafx.scene.control.Button ConfirmButton;

    public String getCurrentFromDate(){
        return currentFromDate;
    }

    public String getCurrentToDate(){
        return currentToDate;
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
        this.queries = new ArrayList<String>(graphController.getCurrentQueries());
        this.lineChart = graphController.getLineChart();

    }

    /**
     * function for when user clicks confirm button -> joins all sql query filters into one string and creates new line
     * @param e Event for when user clicks on confirm button
     */
    @FXML
    public void confirmDate (Event e) {

        String fromdateString = fromDate.getEditor().getText();

        String todateString = toDate.getEditor().getText();

        if (isDateValid(fromdateString) && isDateValid(todateString)){

            this.currentFromDate = fromDate.getEditor().getText();

            this.currentToDate = toDate.getEditor().getText();

            this.graphController.setDates(currentFromDate,currentToDate);


            applyDateRange(currentFromDate,currentToDate);

            remakeLines();

        }
        
        Stage stage = (Stage) ConfirmButton.getScene().getWindow();
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



    public void applyDateRange(String fromDateString, String toDateString){

        String dateFilterString = " date(Date) BETWEEN " + "'" + GraphController.reverseDateOrder(fromDateString) + "'" + " AND " + "'" + GraphController.reverseDateOrder(toDateString) + "'";


        for (int i = 0 ; i < queries.size() ; i++ ){
            String updatedSQL = this.graphController.replaceDateInSQL(queries.get(i),dateFilterString);

            queries.set(i,updatedSQL);
        }

        //this.graphController.updateMetricTotal(this.graphController.calcMetricTotal(this.graphController.convertToTotal(queries.get(0))));

        this.graphController.removeusedQueries();

        this.graphController.updateUsedQuries(queries);

    }






    public void remakeLines(){

        ArrayList<String> seriesNames = new ArrayList<String>();

        for (XYChart.Series<String,Number> series : this.graphController.getLineChart().getData()){
            StringBuilder newSql = new StringBuilder(series.getName());

            int indexEndGroup = newSql.indexOf("| Total");

            if (indexEndGroup != -1){
                newSql.replace(indexEndGroup, newSql.length()  , " ");
            }
            
            seriesNames.add(newSql.toString());
        }


        graphController.removeLineChartData();

        graphController.removeusedQueries();

        for (int i = 0 ; i < queries.size()  ; i++){

            String numberTotal = (graphController.calcMetricTotal(graphController.convertToTotal(queries.get(i))));

            graphController.createLine(queries.get(i),(seriesNames.get(i) + " | Total "+ (numberTotal)), false);



        }

    }




}
