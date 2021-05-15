package app.java.controllers;

import app.Main;
import javafx.print.*;
import javafx.scene.Node;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

import java.util.Optional;

public class PrinterController {
    /**
     * Opens dialog box allowing user to select printer to print to
     * @param myChart the chart to be printed
     * * */
    public void printChart(Node myChart){
        WritableImage snapshot = myChart.snapshot(null,null);
        ImageView ivSnap = new ImageView(snapshot);

        ChoiceDialog dialog = new ChoiceDialog(Printer.getDefaultPrinter(), Printer.getAllPrinters());

        dialog.setHeaderText("Choose the printer");
        dialog.setContentText("Choose a printer from available printers");
        dialog.setTitle("Printer Choice");

        Optional<Printer> opt = dialog.showAndWait();

        if (opt.isPresent()) {
            Printer printer = opt.get();

            PrinterJob job = PrinterJob.createPrinterJob();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);

            final double scaleX = pageLayout.getPrintableWidth() / ivSnap.getImage().getWidth();
            final double scaleY = pageLayout.getPrintableHeight() / ivSnap.getImage().getHeight();
            final double scale = Math.min(scaleX, scaleY);


            // scale when chart too big for page
            if (scale < 1.0) {
                ivSnap.getTransforms().add(new Scale(scale, scale));
            }

            job.setPrinter(printer);

            boolean success = job.printPage(pageLayout, ivSnap);
            if(success){
                job.endJob();
            }
        }



    }
}
