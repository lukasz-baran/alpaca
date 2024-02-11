package com.evolve.gui.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class RetentionFileChooser {
    private final FileChooser instance;
    private final SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();

    private RetentionFileChooser(){
        instance = new FileChooser();
        instance.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
        instance.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("dbf (*.dbf)", "*.dbf"));
    }

    public File showOpenDialog(){
        return showOpenDialog(null);
    }

    public File showOpenDialog(Window ownerWindow){
        File chosenFile = instance.showOpenDialog(ownerWindow);
        if(chosenFile != null){
            //Set the property to the directory of the chosenFile so the fileChooser will open here next
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }

    public File showSaveDialog(){
        return showSaveDialog(null);
    }

    public File showSaveDialog(Window ownerWindow){
        File chosenFile = instance.showSaveDialog(ownerWindow);
        if(chosenFile != null){
            //Set the property to the directory of the chosenFile so the fileChooser will open here next
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }
}