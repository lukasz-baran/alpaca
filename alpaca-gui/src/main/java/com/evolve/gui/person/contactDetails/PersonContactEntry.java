package com.evolve.gui.person.contactDetails;

import com.evolve.domain.PersonContactData;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Setter
@ToString
public class PersonContactEntry {

    public static final Image PHONE_ICON = new Image("icons/phone-32x32.png");
    public static final Image EMAIL_ICON = new Image("icons/email-32x32.png");

    private PersonContactData personContactData;

    public String getData() {
        return personContactData.getData();
    }

    @SuppressWarnings("unused")
    public String getComment() {
        return personContactData.getComment();
    }

    @SuppressWarnings("unused")
    public Image getImageType() {
        return switch (personContactData.getType()) {
            case PHONE -> PHONE_ICON;
            case EMAIL -> EMAIL_ICON;
        };
    }


}
