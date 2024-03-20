package com.evolve.gui.person.address;

import com.evolve.domain.Person;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Getter
@AllArgsConstructor
@Setter
@ToString
public class AddressEntry {
    public static final Image HOME_ADDRESS_ICON = new Image("icons/home-outline.230x256.png");
    public static final Image MAILING_ADDRESS_ICON = new Image("icons/mail.256x209.png");
    public static final Image WORK_ADDRESS_ICON = new Image("icons/work-alt-bag.256x256.png");
    public static final Image OTHER_ADDRESS_ICON = new Image("icons/other.256x256.png");

    private Person.PersonAddress personAddress;

    public String getStreet() {
        return personAddress.getStreet();
    }

    public String getPostalCode() {
        return personAddress.getPostalCode();
    }

    public String getCity() {
        return personAddress.getCity();
    }

    public String getComment() {
        return personAddress.getComment();
    }

    @SuppressWarnings("unused")
    public Image getImageType() {
        final Person.AddressType addressType = Optional.ofNullable(personAddress.getType()).orElse(Person.AddressType.HOME);

        return switch (addressType) {
            case HOME -> HOME_ADDRESS_ICON;
            case MAILING -> MAILING_ADDRESS_ICON;
            case WORK -> WORK_ADDRESS_ICON;
            case OTHER -> OTHER_ADDRESS_ICON;
        };
    }

}