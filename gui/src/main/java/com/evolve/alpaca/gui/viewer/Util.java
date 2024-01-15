package com.evolve.alpaca.gui.viewer;

import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static Alert showCustomErrorDialog(String header, String content, Window owner, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(owner);
        alert.setTitle("Error");

        if (e != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String stackTrace = stringWriter.toString(); // stack trace as a string

            javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(stackTrace);
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);

            alert.getDialogPane().setExpandableContent(expContent);
        }
        return alert;
    }

    public static String getFileExt(String filename) {
        if (filename.contains(".")) {
            Pattern pattern = Pattern.compile("\\.([^.\\\\/]+$)");
            Matcher matcher = pattern.matcher(filename);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return "";
    }

}
