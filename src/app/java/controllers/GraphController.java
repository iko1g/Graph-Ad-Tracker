package app.java.controllers;

import app.java.controllers.popups.DateRangePopupController;
import app.java.controllers.popups.FilterPopupController;
import app.java.util.Database;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GraphController {
    public Button histogramButton;

    public Button printerButton;

    public Button addLineButton;

    public Button dataFilterButton;

    public Button dateRangeButton;

    @FXML
    public Label metricTotal;

    @FXML
    private BorderPane lineChartPane;

    @FXML
    private Label metricLabel;

    private LineChart<String, Number> lineChart;

    private BarChart<String, Number> barChart;

    private final HashMap<String, String> SQL_Query_Map = new HashMap<>();

    public String currentSQLQuery;

    public String currentSQLQuery_Total;
    public String currentSQLQuery_Total_Name;

    private ArrayList<String> currentQueries = new ArrayList<String>();

    public ArrayList<String> getCurrentTotalQueries() {
        return currentTotalQueries;
    }

    public void setCurrentTotalQueries(ArrayList<String> currentTotalQueries) {
        this.currentTotalQueries = currentTotalQueries;
    }

    private ArrayList<String> currentTotalQueries = new ArrayList<String>();


    DateRangePopupController dateRangeController = new DateRangePopupController();

    private boolean wasInitialized = false;

    private boolean customPagesSelected;
    private int customValue;

    @FXML
    ChoiceBox timeGranularity;

    Button selectedButton = null;

    static String selectedGranularity = "Daily";

    private MetricController metricController;

    public void setMetricController(MetricController metricController) {
        this.metricController = metricController;
    }

    public void addToUsedQuries(String query){
        currentQueries.add(query);
    }

    public void updateUsedQuries(ArrayList<String> updated){
        currentQueries.addAll(updated);
    }


    public void removeusedQueries(){
        currentQueries.clear();
    }

    public ArrayList<String> getCurrentQueries(){
        return currentQueries;
    }

    public void initialize() {
        this.configureSQLQueries();
        this.disableAllButtons();
    }

    public LineChart<String, Number> getLineChart() {
        return lineChart;
    }

    private String currentFromDate;

    private String currentToDate;

    public void setDates(String fromDate, String toDate){

        this.currentFromDate = fromDate;
        this.currentToDate = toDate;

    }

    public void setCurrentSQLQuery_Total(String updated){
        this.currentSQLQuery_Total = updated;
    }

    public void visuallySelectClickCostButton() {
        histogramButton.setStyle("-fx-background-color: cyan;");
    }

    public void visuallyDeselectClickCostButton() {
        histogramButton.setStyle("-fx-background-color: transparent");
    }

    public Button getHistogramButton() {
        return this.histogramButton;
    }

    /**
     * TODO: ADD COMMENT HERE
     * @param e - TODO: TO DEFINE HERE
     * @throws IOException - TODO: TO DEFINE HERE
     */
    @FXML
    public void showFilterPopup (Event e) throws IOException {
        String text = ((Button)e.getSource()).getText();

        boolean deleteLastLines;


        if (text.equals("Add Line")){
            deleteLastLines = false;
        }
        else {
            deleteLastLines = true;
        }

        FilterPopupController controller = new FilterPopupController(deleteLastLines);
        controller.setGraphController(this);

        Stage popupWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/resources/fxml/popups/FilterPopup.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        Label filterLabel = (Label)loader.getNamespace().get("filterPopupLabel");
        if (!deleteLastLines) {
            filterLabel.setText("Select Filters For New Line");
        }


        String dateString = "";
        String dateToString = "";

        DatePicker datepickerfrom = (DatePicker) loader.getNamespace().get("fromDateInFilter");
        setconverter(datepickerfrom);

        DatePicker datepickerto = (DatePicker) loader.getNamespace().get("toDateInFilter");
        setconverter(datepickerto);

        if ((selectedGranularity.equals("Daily") || selectedGranularity.equals("Hourly")) && lineChart.getData().get(0).getData().size() != 0){
            dateString = (lineChart.getData().get(0).getData().get(0).getXValue()).substring(0,10);
            dateToString = (lineChart.getData().get(0).getData().get(lineChart.getData().get(0).getData().size()-1).getXValue()).substring(0,10);
            LocalDate date = LocalDate.parse(dateString);
            LocalDate dateTo = LocalDate.parse(dateToString);
            datepickerto.setValue(dateTo);
            datepickerfrom.setValue(date);
        }



        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setScene(new Scene(root));
        popupWindow.show();
    }

    /**
     * TODO: ADD COMMENT HERE
     * @param e - TODO: TO DEFINE HERE
     * @throws IOException - TODO: TO DEFINE HERE
     */
    @FXML
    public void showDateRangePopup (Event e) throws IOException {
        DateRangePopupController controller = new DateRangePopupController();
        controller.setGraphController(this);

        Stage popupWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/resources/fxml/popups/DateRangePopup.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        String dateString = "";
        String dateToString = "";

        DatePicker datepickerfrom = (DatePicker) loader.getNamespace().get("fromDate");
        setconverter(datepickerfrom);

        DatePicker datepickerto = (DatePicker) loader.getNamespace().get("toDate");
        setconverter(datepickerto);

        if ((selectedGranularity.equals("Daily") || selectedGranularity.equals("Hourly")) && lineChart.getData().get(0).getData().size() != 0){
            dateString = (lineChart.getData().get(0).getData().get(0).getXValue()).substring(0, 10);
            dateToString = (lineChart.getData().get(0).getData().get(lineChart.getData().get(0).getData().size() - 1).getXValue()).substring(0, 10);
            LocalDate date = LocalDate.parse(dateString);
            LocalDate dateTo = LocalDate.parse(dateToString);
            datepickerto.setValue(dateTo);
            datepickerfrom.setValue(date);
        }

        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setScene(new Scene(root));
        popupWindow.show();
    }

    @FXML
    public void createBarChart(ActionEvent actionEvent) {

        metricController.visuallyDeselectButton();
        visuallySelectClickCostButton();

        metricLabel.setText("Click Cost Distribution");
        metricTotal.setText("");

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setAnimated(false);
        lineChartPane.setCenter(barChart);

        barChart.setCategoryGap(0);
        barChart.setBarGap(0);

        xAxis.setLabel("Click Cost Intervals (£)");
        yAxis.setLabel("Frequency");

        String metricName = "histogram";

        barChart.setLegendVisible(false);

        computeBarChart();

        currentSQLQuery = SQL_Query_Map.get(metricName);
        currentSQLQuery_Total_Name = metricName;

        addLineButton.setDisable(true);
        dataFilterButton.setDisable(true);
        dateRangeButton.setDisable(true);
        timeGranularity.setDisable(true);
        printerButton.setDisable(false);


    }

    public void createLineChart(String metricName) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true);
        lineChartPane.setCenter(lineChart);

        xAxis.setLabel("Date");

        if (metricName.equals("CPC") || metricName.equals("CPM") || metricName.equals("CPA")
                || metricName.equals("CTR") || metricName.equals("Total Cost")){
            yAxis.setLabel(metricName + " (£)");
        }
        else if (metricName.equals("One Page Viewed")) {
            yAxis.setLabel("Rate of " + metricName + " bounces");
        }
        else {
            yAxis.setLabel("Number of " + metricName);
        }



        this.createLineFromMetric(metricName);

        this.enableAllButtons();
    }

    private void enableAllButtons() {
        this.printerButton.setDisable(false);
        this.addLineButton.setDisable(false);
        this.dataFilterButton.setDisable(false);
        this.dateRangeButton.setDisable(false);
        this.timeGranularity.setDisable(false);
    }

    private void disableAllButtons() {
        this.printerButton.setDisable(true);
        this.addLineButton.setDisable(true);
        this.dataFilterButton.setDisable(true);
        this.dateRangeButton.setDisable(true);
        this.timeGranularity.setDisable(true);
    }

    public void createLineFromMetric(String metricName){

        setDates(null,null);

        removeusedQueries();

        metricLabel.setText(metricName);

        String chosenSQLQuery = SQL_Query_Map.get(metricName);

        if(metricName.equals("Bounce Rate")  || metricName.equals("Bounces")) {
            if(customPagesSelected)
            {
                chosenSQLQuery = chosenSQLQuery.replace("<r>", "PagesViewed <= " + String.valueOf(customValue));
            }
            else
            {
                chosenSQLQuery = chosenSQLQuery.replace("<r>", "(JULIANDAY(ExitDate)-JULIANDAY(EntryDate)) * 86400 <= " + String.valueOf(customValue));
            }

        }

        String chosenSQLQuery_removedG = replaceGranularity(chosenSQLQuery);

        String chosenSQLQuery_removedF = chosenSQLQuery_removedG.replace("<f>" , " ");
        createLine(chosenSQLQuery_removedF,metricName,true);
        currentSQLQuery = chosenSQLQuery;
        currentSQLQuery_Total_Name = metricName;
        updateMetricTotal(calcMetricTotal(convertToTotal(chosenSQLQuery)));

    }

    private String replaceGranularity(String SqlStatement)
    {
        String chosenSQLQuery_removedG;
        if(selectedGranularity.equals("Hourly")) {
            return SqlStatement.replace("<g>" , "strftime('%Y-%m-%d %Hh',Date)");
        } else if(selectedGranularity.equals("Daily")) {
            return SqlStatement.replace("<g>" , "strftime('%Y-%m-%d',Date)");
        } else {
            return SqlStatement.replace("<g>" , "strftime('%Y week %W',Date)");
        }
    }

    /** Function to create a line given a SQL Query and some filters
     *
     */
    public  void computeBarChart(){

        // Series contains all the data points for the line, x = string, y = number
        XYChart.Series<String ,Number> series = new XYChart.Series<>();
        //series.setName("Distribution of Costs");


        for (int i = 0 ; i <= 20 ; i++){

            String queryBuilder = "SELECT COUNT (ClickCost) AS YValue FROM mainTable WHERE ClickCost is not null AND ClickCost >= " + i +" AND ClickCost < " + (i+1);
            String range = String.valueOf(i) ;


            if (i == 20){
                range = ">20";
                queryBuilder = "SELECT COUNT (ClickCost) AS YValue FROM mainTable WHERE ClickCost is not null AND ClickCost > 20 ";
            }

            try {
                Connection connection = Database.getConnection();

                Statement stmt  = connection.createStatement();

                // To prevent Memory hog
                try{
                    ResultSet rs    = stmt.executeQuery(queryBuilder);
                    try{
                        // Goes through every data point and adds them to the series/line according to x and y
                        while (rs.next()) {

                            series.getData().add(new XYChart.Data<>(range,rs.getDouble("YValue")));
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.err.println("Database connection failed (computeBarChart)");
            }
        }

        barChart.getData().add(series);

        for (XYChart.Data<String,Number> d : series.getData()){

            Tooltip t = new Tooltip(d.getYValue().toString() + '\n' + d.getXValue());
            Tooltip.install(d.getNode(), t);
            t.setShowDelay(Duration.seconds(0.1));
            t.setStyle("-fx-font-size: 18");

        }
    }


    public void removeLineChartData(){
        lineChart.getData().removeAll(lineChart.getData());
    }

    /** Function to create a line given a SQL Query and some filters
     *
     * @param SQL_Query the actual query the function will run to get X and Y data points for the chart
     * @param lineName  The name of the line
     * @param DeleteLastLines   Set to true to delete all previous lines/series
     */
    public void createLine(String SQL_Query , String lineName, Boolean DeleteLastLines){

        SQL_Query = replaceGranularity(SQL_Query);



        // Used for later on when we would need 2+ line on a chart
        if (DeleteLastLines){
            lineChart.getData().removeAll(lineChart.getData());
            removeusedQueries();
        }
        addToUsedQuries(SQL_Query);

        // Series contains all the data points for the line, x = string, y = number
        XYChart.Series<String ,Number> series = new XYChart.Series<>();
        series.setName(lineName);

        try {
            Connection connection = Database.getConnection();

            Statement stmt  = connection.createStatement();

            // To prevent Memory hog
            try{
                ResultSet rs    = stmt.executeQuery(SQL_Query);
                try{
                    // Goes through every data point and adds them to the series/line according to x and y
                    while (rs.next()) {
                        String date = String.valueOf(rs.getString("XDate"));
                        series.getData().add(new XYChart.Data<>(date,rs.getDouble("YValue")));
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }



        } catch (SQLException e) {
            System.err.println("Database connection failed (createLine) \nSQL statement: " + SQL_Query);
        }

        //Dummy comment

        // Add the line/series to the chart
        lineChart.getData().add(series);


        for (XYChart.Data<String,Number> d : series.getData()){

            Tooltip t = new Tooltip(d.getYValue().toString() + '\n' + d.getXValue());
            Tooltip.install(d.getNode(), t);
            t.setShowDelay(Duration.seconds(0.1));
            t.setStyle("-fx-font-size: 18");

        }

        


    }


    public void setconverter(DatePicker datePicker){
        String pattern = "dd/MM/yyyy";

        datePicker.setPromptText(pattern.toLowerCase());

        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }




    public static Date todate(String datestring){
        String startDateString = datestring;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        try {
            startDate = df.parse(startDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }



    public static String reverseDateOrder (String oldDateString)  {
        final String OLD_FORMAT = "dd/MM/yyyy";
        final String NEW_FORMAT = "yyyy/MM/dd";

        String newDateString = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
            newDateString = newDateString.replace("/","-");
        }
        catch (ParseException e ){
            e.printStackTrace();
        }
        return  newDateString;
    }


    public String calcMetricTotal (String chosenSQLQuery){

        chosenSQLQuery = chosenSQLQuery.replace("<f>" , " ");

        if(currentSQLQuery_Total_Name.equals("Bounce Rate") || currentSQLQuery_Total_Name.equals("Bounces")) {
            if(customPagesSelected)
            {
                chosenSQLQuery = chosenSQLQuery.replace("<r>", "PagesViewed <= " + String.valueOf(customValue));
            }
            else
            {
                chosenSQLQuery = chosenSQLQuery.replace("<r>", "(JULIANDAY(ExitDate)-JULIANDAY(EntryDate)) * 86400 <= " + String.valueOf(customValue));
            }
        }

        String Amount = "";

        //chosenSQLQuery = convertToTotal(chosenSQLQuery);

        try {
            Connection connection = Database.getConnection();

            Statement stmt  = connection.createStatement();
            ResultSet rs;

            // To prevent Memory hog
            try{
                rs    = stmt.executeQuery(chosenSQLQuery);

                try{
                    while (rs.next()) {
                        Amount = String.valueOf(rs.getString("YValue"));

                    }


                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }

        } catch (SQLException e) {
            System.err.println("Database connection failed (updateMetricTotal) \nSQL statement: " + chosenSQLQuery);        }


        if (currentSQLQuery_Total_Name.equals("CPC") || currentSQLQuery_Total_Name.equals("CPM") || currentSQLQuery_Total_Name.equals("CPA")
                || currentSQLQuery_Total_Name.equals("CTR") || currentSQLQuery_Total_Name.equals("Total Cost")){
            double numberTotal = Double.parseDouble(Amount);
            numberTotal = (double)Math.round(numberTotal * 10000d) / 10000d;
            return  ("£" + String.valueOf(numberTotal));
        }
        if (currentSQLQuery_Total_Name.equals("Bounce Rate") ){
            double numberTotal = Double.parseDouble(Amount);
            numberTotal = (double)Math.round(numberTotal * 10000d) / 10000d;
            return  (String.valueOf(numberTotal));
        }
        else {
            return Amount;
        }

    }

    public void updateMetricTotal (String amount){

        metricTotal.setText(amount);

    }

    public void selectGranularity(ActionEvent e) {
        ChoiceBox choiceBox = (ChoiceBox) e.getSource();
        Object selectedItem = choiceBox.getSelectionModel().getSelectedItem();
        timeGranularity.setValue(selectedItem);
        selectedGranularity = (String) selectedItem;
        try
        {
            createLineFromMetric(currentSQLQuery_Total_Name);
        }
        catch(Exception ignored){
            //ignored
        }
    }

    public void printerButtonClick(Event e){
        PrinterController pc = new PrinterController();
        pc.printChart(this.lineChartPane.getCenter());
    }

    public void setWasInitialized()
    {
        wasInitialized = true;
    }

    public boolean getWasInitialized() {
        return wasInitialized;
    }

    public void setCustomBounce(boolean type, int value )
    {
        customPagesSelected = type;
        customValue = value;
    }

    public boolean getUsingPagesBoolean()
    {
        return customPagesSelected;
    }

    public int getCustomValue()
    {
        return customValue;
    }

    private void configureSQLQueries() {
        SQL_Query_Map.put("Impressions", "SELECT <g> AS XDate, COUNT (ImpressionCost) AS YValue FROM mainTable WHERE ClickCost is null  GROUP BY <g>");
        SQL_Query_Map.put("Clicks", "SELECT <g> AS XDate, COUNT(ClickCost) AS YValue FROM mainTable <f> GROUP BY <g>");
        SQL_Query_Map.put("Uniques", "SELECT <g> AS XDate, COUNT (DISTINCT ID) AS YValue FROM mainTable WHERE ClickCost IS NOT NULL GROUP BY <g>");
        SQL_Query_Map.put("Bounces", "SELECT <g> AS XDate, COUNT(PagesViewed) AS YValue FROM mainTable WHERE (ExitDate == 'n/a' OR  <r> )GROUP BY <g>");
        SQL_Query_Map.put("Conversions", "SELECT <g> AS XDate, COUNT (Conversion) AS YValue FROM mainTable WHERE Conversion = 'Yes' GROUP BY <g>");
        SQL_Query_Map.put("Total Cost", "SELECT <g> AS XDate, SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end) + SUM (ClickCost) AS YValue FROM mainTable <f> GROUP BY <g>");
        SQL_Query_Map.put("CPA", "SELECT <g> AS XDate,  (SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end) + SUM (ClickCost))/(SUM (CASE WHEN Conversion = 'Yes' then 1 else 0 end)) AS YValue FROM mainTable <f> GROUP BY <g>");
        SQL_Query_Map.put("CTR", "SELECT <g> AS XDate, CAST(COUNT(ClickCost) AS Float)/CAST(COUNT(ImpressionCost) AS Float) AS YValue FROM mainTable <f> GROUP BY <g>");
        SQL_Query_Map.put("CPM", "SELECT <g> AS XDate, SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end) / COUNT (ImpressionCost) * 1000 AS YValue FROM mainTable <f> GROUP BY <g>");
        SQL_Query_Map.put("CPC", "SELECT <g> AS XDate, (SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end) + SUM (ClickCost))/COUNT (ClickCost) AS YValue FROM mainTable <f> GROUP BY <g>");
        // This combines both requirements
        SQL_Query_Map.put("Bounce Rate", "SELECT <g> AS XDate, 1.0 * SUM (CASE WHEN ExitDate == 'n/a' OR <r> THEN 1 ELSE 0 END)/ COUNT(PagesViewed) AS YValue FROM mainTable <f> GROUP BY <g>");

        }






    public String replaceDateInSQL(String sqlStatment, String newDateRange){

        int indexOfDate = sqlStatment.indexOf("date(Date) BETWEEN");
        StringBuilder newSql = new StringBuilder(sqlStatment);

        if (indexOfDate == -1){

            if (!sqlStatment.contains("WHERE")){
                int indexOfWhere = sqlStatment.indexOf("mainTable");
                newSql.replace(indexOfWhere + 10, indexOfWhere + 11, "WHERE " + newDateRange);

                return newSql.toString();
            }

            sqlStatment = sqlStatment.replaceAll("WHERE", "WHERE " + (newDateRange+ " AND "));
            sqlStatment = sqlStatment.replaceAll("<f>", "WHERE " + (newDateRange+ " "));

            return sqlStatment;
        }
        else {

            newSql.replace(indexOfDate,indexOfDate + 48, newDateRange);
            return newSql.toString();
        }
    }

    public String convertToTotal(String sqlStatment){

        StringBuilder newSql = new StringBuilder(sqlStatment);

        int indexEnd = newSql.indexOf("XDate,");
        newSql.replace(6, indexEnd + 6 , " ");

        int indexEndGroup = newSql.indexOf("GROUP BY");

        newSql.replace(indexEndGroup, newSql.length()  , " ");


        return newSql.toString();

    }
}
