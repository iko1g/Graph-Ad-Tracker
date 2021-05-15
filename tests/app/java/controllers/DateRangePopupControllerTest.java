package app.java.controllers;

import app.java.controllers.GraphController;
import app.java.controllers.popups.DateRangePopupController;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DateRangePopupControllerTest {

    DateRangePopupController dateRangePopupController;
    JFXPanel panel = new JFXPanel();

    @Before
    public void setUp() throws Exception {
        dateRangePopupController = new DateRangePopupController();
    }


    @Test
    public void isDateValidTest() {

        String date1 = "23/01/2015";
        String date2 = "f3/01/2015";
        String date3 = "23/51/2015";
        String date4 = "23/01/20152";

        assertTrue(dateRangePopupController.isDateValid(date1));
        assertFalse(dateRangePopupController.isDateValid(date2));
        assertFalse(dateRangePopupController.isDateValid(date3));
        assertFalse(dateRangePopupController.isDateValid(date4));
    }
}
