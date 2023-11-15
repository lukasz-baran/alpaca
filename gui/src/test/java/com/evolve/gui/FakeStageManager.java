package com.evolve.gui;

import javafx.beans.property.SimpleObjectProperty;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Primary
public class FakeStageManager extends StageManager {
    public FakeStageManager(FxWeaver fxWeaver, SimpleObjectProperty<File> lastKnownDirectoryProperty) {
        super(fxWeaver, lastKnownDirectoryProperty);
    }
}
