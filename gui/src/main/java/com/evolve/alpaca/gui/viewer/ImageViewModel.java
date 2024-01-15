package com.evolve.alpaca.gui.viewer;

import com.evolve.gui.documents.DocumentEntry;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageViewModel {

    private final ReadOnlyStringWrapper status = new ReadOnlyStringWrapper("Ready.");
    private final ReadOnlyObjectWrapper<ImageModel> selectedImageModelWrapper = new ReadOnlyObjectWrapper<>();
    @Getter
    private final String[] supportedReadExtensions
            = new String[]{"bmp", "png", "gif", "jpeg", "jpg", "tiff", "ico", "cur", "psd", "psb" /*, "svg", "wmf"*/};
    @Getter
    private final String[] supportedWriteExtensions
            = new String[]{"bmp", "png", "gif", "jpeg", "jpg", "tiff", "ico"};

    public ImageViewModel() {
        selectedImageModelProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null && !newValue.hasOriginal()) {
                // clear caches
                oldValue.unsetImage();
            }
        }));
    }

    public ReadOnlyObjectProperty<ImageModel> selectedImageModelProperty() {
        return selectedImageModelWrapper.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty statusProperty() {
        return status.getReadOnlyProperty();
    }

    public void loadImage(InputStream imageInputStream, DocumentEntry documentEntry) throws IOException {
        ImageModel imageModel = new ImageModel(imageInputStream, documentEntry);

        setSelectedImage(imageModel);
    }

    public ImageModel getSelectedImageModel() {
        return selectedImageModelWrapper.get();
    }

    private String formatTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    private String formatFileSize(long bytes) {
        long fileSize = bytes;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        if (fileSize > 1e3 && fileSize < 1e6) {
            return Math.round(fileSize / 1e3) + " KB";
        }
        if (fileSize >= 1e6) {
            return decimalFormat.format(fileSize / 1e6) + " MB";
        }
        return fileSize + " B";
    }

    private void setSelectedImage(ImageModel imageModel) {
        status.unbind();

        if (imageModel == null) {
            selectedImageModelWrapper.set(null);
            status.set("Ready.");
        } else {
            selectedImageModelWrapper.set(imageModel);

            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(imageModel.getName());
            if (imageModel.getImage() != null) {
                stringBuilder
                        .append("  |  ").append(imageModel.getFormat())
                        .append("  |  ").append(getMegapixelCount(imageModel))
                        .append("  |  ").append(formatFileSize(imageModel.getFileSize()))
                        .append(imageModel.hasOriginal()
                                ? " (" + formatFileSize(imageModel.getOriginal().getFileSize()) + ")"
                                : "")
                        .append("  |  Created: ").append(formatTime(imageModel.getDateCreated()));
            } else {
                stringBuilder.append("  |  Error");
            }
            status.set(stringBuilder.toString());
        }
    }

    private String getMegapixelCount(ImageModel imageModel) {
        double megapixel
                = (imageModel.hasOriginal() ? imageModel.getOriginal().getWidth() : imageModel.getWidth())
                * (imageModel.hasOriginal() ? imageModel.getOriginal().getHeight() : imageModel.getHeight())
                / 1e6;

        return new DecimalFormat("#.##").format(megapixel) + "MP";
    }
}

