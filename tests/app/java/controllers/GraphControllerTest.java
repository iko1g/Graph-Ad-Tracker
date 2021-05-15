package app.java.controllers;

import app.java.util.Database;
import javafx.embed.swing.JFXPanel;
import javafx.scene.chart.XYChart;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class GraphControllerTest {

    GraphController graphController;
    JFXPanel panel = new JFXPanel();

    @Before
    public void setUp() throws Exception {
        graphController = new GraphController();
    }

    @Test
    public void barchartTest() {
        //graphController.createLineChart("");

        String queryBuilder = "SELECT COUNT (ClickCost) AS YValue FROM mainTable WHERE ClickCost is not null AND ClickCost >= " + 0 +" AND ClickCost < " + 1;

        try {
            Connection connection = Database.getConnection();

            Statement stmt  = connection.createStatement();

            // To prevent Memory hog
            try{
                ResultSet rs    = stmt.executeQuery(queryBuilder);
                try{
                    // Goes through every data point and adds them to the series/line according to x and y
                    while (rs.next()) {

                        rs.getDouble("YValue");
                        assertEquals(String.valueOf(12044.0), String.valueOf(rs.getDouble("YValue")));
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



    @Test
    public void testSetMetricTotal() {

        graphController.currentSQLQuery_Total_Name="Impressions";
        assertEquals("Impressions did not match 486104",String.valueOf(486104),
                graphController.calcMetricTotal("SELECT COUNT (ImpressionCost) AS YValue FROM mainTable WHERE ClickCost is null  "));

        graphController.currentSQLQuery_Total_Name="Clicks";
        assertEquals("Clicks did not match 23923",String.valueOf(23923),
                graphController.calcMetricTotal("SELECT COUNT (ClickCost) AS YValue FROM mainTable WHERE ClickCost is not null  "));

        graphController.currentSQLQuery_Total_Name="Conversions";
        assertEquals("Conversions did not match 2026",String.valueOf(2026),
                graphController.calcMetricTotal("SELECT COUNT (Conversion) AS YValue FROM mainTable WHERE Conversion = 'Yes'"));

        // THIS WILL CHANGE WITH LATEST PUSH HAS TO INCLUDE £ + ALL AVERAGES ARE ROUNDED IN THIS FUNCTION
        graphController.currentSQLQuery_Total_Name="Total Cost";
        assertEquals("Total Cost did not match 118097.9212","£"+String.valueOf(118097.9212),
                graphController.calcMetricTotal("SELECT  SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end) + SUM (ClickCost) AS YValue FROM mainTable"));


        graphController.currentSQLQuery_Total_Name="Uniques";
        assertEquals("Uniques did not match 23806",String.valueOf(23806),
                graphController.calcMetricTotal("SELECT  COUNT (DISTINCT ID) AS YValue FROM mainTable WHERE ClickCost IS NOT NULL"));

        graphController.currentSQLQuery_Total_Name="CPA";
        assertEquals("CPA did not match 58.2912","£"+String.valueOf(58.2912),
                graphController.calcMetricTotal("SELECT (SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end) + SUM (ClickCost))/(SUM (CASE WHEN Conversion = \"Yes\" then 1 else 0 end)) AS YValue FROM mainTable"));

        graphController.currentSQLQuery_Total_Name="CTR";
        assertEquals("CTR did not match 0.0469","£"+String.valueOf(0.0469),
                graphController.calcMetricTotal("SELECT CAST(COUNT(ClickCost) AS Float)/CAST(COUNT(ImpressionCost) AS Float) AS YValue FROM mainTable"));

        graphController.currentSQLQuery_Total_Name="CPM";
        assertEquals("CPM did not match 0.955","£"+String.valueOf(0.955),
                graphController.calcMetricTotal("SELECT  (SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end)) / COUNT (ImpressionCost) * 1000 AS YValue FROM mainTable"));

        graphController.currentSQLQuery_Total_Name="CPC";
        assertEquals("CPC did not match 4.9366","£"+String.valueOf(4.9366),
                graphController.calcMetricTotal("SELECT (SUM (CASE WHEN ClickCost is null then ImpressionCost else 0 end) + SUM (coalesce(ClickCost, 0)))/COUNT (ClickCost) AS YValue FROM mainTable\n"));


    }

    @Test
    public void converttoTotalTest() {

        assertEquals("SELECT  COUNT (ImpressionCost) AS YValue FROM mainTable WHERE ClickCost is null   ",
                graphController.convertToTotal("SELECT strftime('%Y-%m-%d',Date) AS XDate, COUNT (ImpressionCost) AS YValue FROM mainTable WHERE ClickCost is null  GROUP BY strftime('%Y-%m-%d',Date)"));

    }

    @Test
    public void reverseDateTest() {

        assertEquals("2015-01-08",
                GraphController.reverseDateOrder("08/01/2015"));

    }
}
