package app.java.util;

import java.io.*;
import java.sql.*;

/**
 * Class for the database.
 * Contains method for getting data from the logs into one central database.
 */
public class Database {

    static Connection connection = null;

    /**
     * TODO: ADD COMMENT HERE
     * @param queryString - TODO: TO DEFINE HERE
     * @return - TODO: TO DEFINE HERE
     */
    public static ResultSet query(String queryString) {
        Statement stmt  = null;

        try {
            stmt = Database.getConnection().createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            return stmt.executeQuery(queryString);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    /**
     * TODO: ADD COMMENT HERE
     * @return - TODO: TO DEFINE HERE
     */
    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("sqlite JDBC not installed");
        }

        File adDatabase = new File("AdLogs.db");

        if (!adDatabase.exists()) {
            try {
                adDatabase.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create database on disk");
            }
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:AdLogs.db");
        } catch(Exception e) {
            System.err.println("Failed to connect to database");
        }

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("AutoCommit failed to setup");
        }

        Database.setupTables();

        return connection;
    }

    public static boolean checkIfDataExists() throws SQLException {
        Statement stmt = Database.getConnection().createStatement();

        stmt.execute("SELECT * FROM mainTable LIMIT 10");

        return stmt.getResultSet().next();

//        if (stmt.getResultSet() == null) {
//            return false;
//        } else {
//            return true;
//        }
    }

    /**
     * TODO: ADD COMMENT HERE
     */
    private static void setupTables() {
        // SQL statement for creating a new table
        String sql1 = "CREATE TABLE IF NOT EXISTS impressions ("
                + "Date Text,"
                + "ID BigInt,"
                + "Gender Text,"
                + "Age Text,"
                + "Income Text,"
                + "Context Text,"
                + "ImpressionCost Real"
                + ");";

        // SQL statement for creating a new table
        String sql2 = "CREATE TABLE IF NOT EXISTS clicks ("
                + "Date Text,"
                + "ID BigInt,"
                + "ClickCost Real"
                + ");";

        // SQL statement for creating a new table
        String sql3 = "CREATE TABLE IF NOT EXISTS servers ("
                + "EntryDate Text,"
                + "ID BigInt,"
                + "ExitDate Text,"
                + "PagesViewed SmallInt,"
                + "Conversion Text"
                + ");";

        String sql4 = "CREATE TABLE IF NOT EXISTS mainTable ("
                + "Date Text,"
                + "ID BigInt,"
                + "Gender Text,"
                + "Age Text,"
                + "Income Text,"
                + "Context Text,"
                + "ImpressionCost Real,"
                //+ "Date Text,"
                + "ClickCost Real,"
                + "EntryDate Text,"
                + "ExitDate Text,"
                + "PagesViewed SmallInt,"
                + "Conversion Text"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
            stmt.execute(sql4);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void cleanMainTable()
    {
        try {
            PreparedStatement clearStatement = connection.prepareStatement("DELETE FROM mainTable");
            clearStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
