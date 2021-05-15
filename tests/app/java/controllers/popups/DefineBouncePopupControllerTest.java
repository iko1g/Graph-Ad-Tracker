package app.java.controllers.popups;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefineBouncePopupControllerTest {

    DefineBouncePopupController defineBouncePopupController;

    @Before
    public void setUp() throws Exception {
        defineBouncePopupController = new DefineBouncePopupController();
    }


    @Test
    public void isValidInputTest(){
        String bounce1 = "5";
        String bounce2 = "0";
        String bounce3 = "-10";
        String bounce4 = "jajaja";
        String bounce5 = "092384";

        assertTrue("Incorrect result on 5", defineBouncePopupController.isStringValid(bounce1));
        assertFalse("Incorrect result on 0", defineBouncePopupController.isStringValid(bounce2));
        assertFalse("Incorrect result on -10", defineBouncePopupController.isStringValid(bounce3));
        assertFalse("Incorrect result on jajaja", defineBouncePopupController.isStringValid(bounce4));
        assertFalse("Incorrect result on 092384", defineBouncePopupController.isStringValid(bounce5));
    }

}