package com.evolve.alpaca.gui.viewer;

import com.evolve.alpaca.document.DocumentEntry;
import com.evolve.alpaca.utils.FileNameUtils;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.time.ZoneId;


@Getter
public class ImageModel {

    private Image image;
    @Setter
    private ImageModel original = null;
    private final String name;
    private final long dateCreated;
    private final long fileSize;

    public ImageModel(InputStream imageInputStream, DocumentEntry documentEntry) {
        this.name = documentEntry.getFileName();
        this.image = new Image(imageInputStream);
        this.dateCreated = documentEntry.getDateAdded().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.fileSize = documentEntry.getLength();
    }

    public void unsetImage() {
        image = null;

        if (hasOriginal()) {
            original = null;
        }
    }

    public String getFormat() {
        return FileNameUtils.getFileExt(name).toUpperCase();
    }

    public double getWidth() {
        return getImage() != null ? getImage().getWidth() : 0;
    }

    public double getHeight() {
        return getImage() != null ? getImage().getHeight() : 0;
    }

    public boolean hasOriginal() {
        return original != null;
    }

    /**
     * @return Resolution in the format width x height px
     */
    public String getResolution() {
        return Math.round(getWidth()) + " x " + Math.round(getHeight()) + " px";
    }

    public double getAspectRatio() {
        return getWidth() / getHeight();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

