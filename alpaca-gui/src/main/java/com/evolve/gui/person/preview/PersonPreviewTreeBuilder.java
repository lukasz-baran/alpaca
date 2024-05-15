package com.evolve.gui.person.preview;

import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.domain.RegistryNumber;
import javafx.scene.control.TreeItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@RequiredArgsConstructor
public class PersonPreviewTreeBuilder {
    public static final String MISSING = "<brak>";

    private final boolean expand;

    public TreeItem<PersonTreeItem> of(Person person) {
        final TreeItem<PersonTreeItem> root = new TreeItem<>(rootNodeValue(person));
        root.setExpanded(true);

        addFirstName(root, person);
        addSecondName(root, person);
        addLastName(root, person);
        addGender(root, person);
        addRegistryNumber(root, person);
        addPersonAddresses(root, person);
        addAuthorizedPersons(root, person);

        return root;
    }


    PersonTreeItem rootNodeValue(Person p) {
        Function<Person, Optional<String>> rootText = (Person person) -> {
            final StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotEmpty(person.getFirstName())) {
                sb.append(person.getFirstName()).append(" ");
            }
            if (StringUtils.isNotEmpty(person.getLastName())) {
                sb.append(person.getLastName()).append(" ");
            }
            sb.append("(").append(person.getPersonId()).append(")");
            return Optional.of(sb.toString());
        };

        return new PersonTreeItem("ID", rootText, p);
    }

    void addFirstName(TreeItem<PersonTreeItem> rootNode, Person person) {
        if (StringUtils.isNotEmpty(person.getFirstName()) || expand) {
            var newItem = new PersonTreeItem("Imię", p -> Optional.ofNullable(trimToNull(p.getFirstName())), person);

            rootNode.getChildren().add(new TreeItem<>(newItem));
        }
    }

    void addSecondName(TreeItem<PersonTreeItem> rootNode, Person person) {
        if (StringUtils.isNotEmpty(person.getSecondName()) || expand) {
            var newItem = new PersonTreeItem("Drugie imię", p -> Optional.ofNullable(trimToNull(p.getSecondName())), person);

            rootNode.getChildren().add(new TreeItem<>(newItem));
        }
    }

    void addLastName(TreeItem<PersonTreeItem> rootNode, Person person) {
        if (StringUtils.isNotEmpty(person.getLastName()) || expand) {
            var newItem = new PersonTreeItem("Nazwisko", p -> Optional.ofNullable(trimToNull(p.getLastName())), person);

            rootNode.getChildren().add(new TreeItem<>(newItem));
        }
    }

    void addGender(TreeItem<PersonTreeItem> rootNode, Person person) {
        if (person.getGender() != null || expand) {
            var newItem = new PersonTreeItem("Płeć",
                    p -> Optional.ofNullable(p.getGender()).map(Person.Gender::getName), person);

            rootNode.getChildren().add(new TreeItem<>(newItem));
        }
    }

    void addRegistryNumber(TreeItem<PersonTreeItem> rootNode, Person person) {
        final Optional<Integer> registryNumber = Optional.ofNullable(person.getRegistryNumber())
                .map(RegistryNumber::getRegistryNum);

        if (registryNumber.isPresent() || expand) {
            var newItem = new PersonTreeItem("Kartoteka",
                    p -> Optional.ofNullable(p.getRegistryNumber()).flatMap(RegistryNumber::getNumber)
                            .map(Object::toString), person);

            rootNode.getChildren().add(new TreeItem<>(newItem));
        }
    }

    void addPersonAddresses(TreeItem<PersonTreeItem> rootNode, Person person) {
        final TreeItem<PersonTreeItem> addressesTreeItem = new TreeItem<>(new PersonTreeItem("Adresy",
                p -> Optional.ofNullable(p.getAddresses()).map(addresses -> "(" + addresses.size() + ")"), person));
        addressesTreeItem.setExpanded(true);
        rootNode.getChildren().add(addressesTreeItem);

        final List<Person.PersonAddress> addressList = emptyIfNull(person.getAddresses());
        for (final Person.PersonAddress personAddress : addressList) {
            final TreeItem<PersonTreeItem> newItem = new TreeItem<>(new PersonTreeItem("Adres",
                    p -> Optional.of(Address.toConcatenatedAddress(personAddress)), person));
            addressesTreeItem.getChildren().add(newItem);

            final TreeItem<PersonTreeItem> itemComment = new TreeItem<>(new PersonTreeItem("Komentarz",
                    p -> Optional.ofNullable(personAddress.getComment()), person));
            newItem.getChildren().add(itemComment);

            final TreeItem<PersonTreeItem> itemType = new TreeItem<>(new PersonTreeItem("Typ",
                    p -> Optional.ofNullable(personAddress.getType()).map(Person.AddressType::getName), person));
            newItem.getChildren().add(itemType);

        }
    }

    private void addAuthorizedPersons(TreeItem<PersonTreeItem> root, Person person) {
        final TreeItem<PersonTreeItem> authorizedPersonsTreeItem = new TreeItem<>(new PersonTreeItem("Osoby upoważnione",
                p -> Optional.ofNullable(p.getAuthorizedPersons()).map(authorizedPeople ->
                        "(" + authorizedPeople.size() + ")"), person));

        authorizedPersonsTreeItem.setExpanded(true);
        root.getChildren().add(authorizedPersonsTreeItem);
    }



}
