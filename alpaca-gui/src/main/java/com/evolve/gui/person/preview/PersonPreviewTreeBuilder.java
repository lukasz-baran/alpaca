package com.evolve.gui.person.preview;

import com.evolve.domain.*;
import javafx.scene.control.TreeItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@RequiredArgsConstructor
public class PersonPreviewTreeBuilder {
    public static final String MISSING = "<brak>";

    private final boolean expand;
    private final Person person;

    public PersonPreview of() {
        final TreeItem<PersonTreeItem> root = rootNode();
        root.setExpanded(true);

        addFirstName(root);
        addSecondName(root);
        addLastName(root);
        addPreviousNames(root);
        addGender(root);
        addRegistryNumber(root);
        addOldRegistryNumber(root);
        addRetired(root);
        addExemptFromFees(root);
        addPersonAddresses(root);
        addAuthorizedPersons(root);
        addContacts(root);
        addStatusChanges(root);
        addBankAccounts(root);
        addPesel(root);
        addIdNumber(root);

        return new PersonPreview(expand, person, root, getMapByTags(root));
    }

    Map<String, List<Object>> getMapByTags(TreeItem<PersonTreeItem> root) {
        final Map<String, List<Object>> byTags = new HashMap<>();
        addNextLevel(byTags, new ArrayList<>(), root);
        return byTags;
    }

    void addNextLevel(Map<String, List<Object>> result,  List<String> breadcrumb, TreeItem<PersonTreeItem> node) {
        final List<String> newBreadcrumb = new ArrayList<>(breadcrumb);
        final PersonTreeItem treeItem = node.getValue();
        newBreadcrumb.add(treeItem.getTag());

        final String key = String.join(".", newBreadcrumb);

        if (newBreadcrumb.size() == 3) {
            if (treeItem.getValue().isPresent()) {
                final List<Object> values = result.getOrDefault(key, new ArrayList<>());
                values.add(treeItem.getValue().get());
                result.put(key, values);
            }

        } else {

            final Object value = treeItem.getValue().orElse(treeItem.getDisplayText());
            result.put(key, List.of(value));

            if (!node.getChildren().isEmpty()) {
                for (TreeItem<PersonTreeItem> child : node.getChildren()) {
                    addNextLevel(result, newBreadcrumb, child);
                }
            }
        }
    }


    TreeItem<PersonTreeItem> rootNode() {
        final Function<Person, Optional<String>> rootText = (Person person) -> {
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

        return addSubNode(null, "ID", rootText);
    }

    TreeItem<PersonTreeItem> addSubNode(TreeItem<PersonTreeItem> node, String tag,
                                             Function<Person, Optional<String>> toText) {
        return addSubNode(node, tag, Optional.empty(), toText);

    }

    TreeItem<PersonTreeItem> addSubNode(TreeItem<PersonTreeItem> node, String tag,
                                        Optional<Object> value,
                                        Function<Person, Optional<String>> toText) {
        var newItem = PersonTreeItem.withTag(tag, value, toText.apply(person).orElse(MISSING));
        final TreeItem<PersonTreeItem> newNode = new TreeItem<>(newItem);
        if (node != null) {
            node.getChildren().add(newNode);
        }
        return newNode;
    }

    void addFirstName(TreeItem<PersonTreeItem> rootNode) {
        if (StringUtils.isNotEmpty(person.getFirstName()) || expand) {
            addSubNode(rootNode, "Imię", p -> Optional.ofNullable(trimToNull(p.getFirstName())));
        }
    }

    void addSecondName(TreeItem<PersonTreeItem> rootNode) {
        if (StringUtils.isNotEmpty(person.getSecondName()) || expand) {
            addSubNode(rootNode, "Drugie imię", p -> Optional.ofNullable(trimToNull(p.getSecondName())));
        }
    }

    void addLastName(TreeItem<PersonTreeItem> rootNode) {
        if (StringUtils.isNotEmpty(person.getLastName()) || expand) {
            addSubNode(rootNode, "Nazwisko", p -> Optional.ofNullable(trimToNull(p.getLastName())));
        }
    }

    void addGender(TreeItem<PersonTreeItem> rootNode) {
        if (person.getGender() != null || expand) {
            addSubNode(rootNode, "Płeć",
                    p -> Optional.ofNullable(p.getGender()).map(Person.Gender::getName));
        }
    }

    void addRegistryNumber(TreeItem<PersonTreeItem> rootNode) {
        final Optional<Integer> registryNumber = Optional.ofNullable(person.getRegistryNumber())
                .map(RegistryNumber::getRegistryNum);

        if (registryNumber.isPresent() || expand) {
            addSubNode(rootNode, "Kartoteka",
                    p -> Optional.ofNullable(p.getRegistryNumber()).flatMap(RegistryNumber::getNumber)
                            .map(Object::toString));
        }
    }

    void addOldRegistryNumber(TreeItem<PersonTreeItem> root) {
        final Optional<Integer> oldRegistryNumber = Optional.ofNullable(person.getOldRegistryNumber())
                .map(RegistryNumber::getRegistryNum);

        if (oldRegistryNumber.isPresent() || expand) {
            addSubNode(root, "Stara Kartoteka",
                    p -> Optional.ofNullable(p.getOldRegistryNumber()).flatMap(RegistryNumber::getNumber)
                            .map(Object::toString));
        }
    }

    void addRetired(TreeItem<PersonTreeItem> root) {
        addSubNode(root, "Emeryt",
                p -> Optional.ofNullable(person.getRetired())
                        .map(retired -> BooleanUtils.toString(retired, "Tak", "Nie")));
    }

    void addExemptFromFees(TreeItem<PersonTreeItem> root) {
        addSubNode(root, "Zwolniony",
                p -> Optional.ofNullable(person.getExemptFromFees())
                        .map(exempt -> BooleanUtils.toString(exempt, "Tak", "Nie")));
    }

    void addPreviousNames(TreeItem<PersonTreeItem> rootNode) {
        final List<String> listOfNames = emptyIfNull(person.getPreviousLastNames());
        final TreeItem<PersonTreeItem> previousNamesTreeItem =
                addSubNode(rootNode, "Poprzednie nazwiska",
                        p -> Optional.ofNullable(listOfNames).map(list -> "(" + list.size() + ")"));
        previousNamesTreeItem.setExpanded(true);

        for (final String lastName : listOfNames) {
            addSubNode(previousNamesTreeItem, "Nazwisko",  p -> Optional.of(lastName));
        }
    }

    void addPersonAddresses(TreeItem<PersonTreeItem> rootNode) {
        final TreeItem<PersonTreeItem> addressesTreeItem =
                addSubNode(rootNode, "Adresy", p -> Optional.ofNullable(p.getAddresses()).map(addresses -> "(" + addresses.size() + ")"));
        addressesTreeItem.setExpanded(true);

        final List<Person.PersonAddress> addressList = emptyIfNull(person.getAddresses());
        for (final Person.PersonAddress personAddress : addressList) {
            final TreeItem<PersonTreeItem> newItem = addSubNode(addressesTreeItem,
                    "Adres", Optional.of(personAddress), p -> Optional.of(Address.toConcatenatedAddress(personAddress)));

            addSubNode(newItem, "Komentarz", p -> Optional.ofNullable(personAddress.getComment()));

            addSubNode(newItem, "Typ", p -> Optional.ofNullable(personAddress.getType()).map(Person.AddressType::getName));
        }
    }

    void addAuthorizedPersons(TreeItem<PersonTreeItem> root) {
        final TreeItem<PersonTreeItem> authorizedPersonsTreeItem =
            addSubNode(root, "Osoby upoważnione",
                    p -> Optional.ofNullable(p.getAuthorizedPersons()).map(authorizedPeople -> "(" + authorizedPeople.size() + ")"));

        authorizedPersonsTreeItem.setExpanded(true);

        final List<Person.AuthorizedPerson> authorizedPersonList = emptyIfNull(person.getAuthorizedPersons());
        for (final Person.AuthorizedPerson authorizedPerson : authorizedPersonList) {

            final TreeItem<PersonTreeItem> newItem = addSubNode(authorizedPersonsTreeItem,
                    "Osoba upoważniona", Optional.of(authorizedPerson),
                    p -> Optional.of(Person.AuthorizedPerson.toConcatenated(authorizedPerson)));

            addSubNode(newItem, "Relacja", p -> Optional.ofNullable(authorizedPerson.getRelation()));

            addSubNode(newItem, "Komentarz", p -> Optional.ofNullable(authorizedPerson.getComment()));
        }
    }

    void addContacts(TreeItem<PersonTreeItem> root) {
        final TreeItem<PersonTreeItem> contactDataTreeItem =
                addSubNode(root, "Dane kontaktowe", p -> Optional.ofNullable(p.getContactData()).map(contactData ->
                                "(" + contactData.size() + ")"));
        contactDataTreeItem.setExpanded(true);

        final List<PersonContactData> contactList = emptyIfNull(person.getContactData());
        for (final PersonContactData contactData : contactList) {
            final String tag = contactData.getType().toString();

            final TreeItem<PersonTreeItem> contactNode = addSubNode(contactDataTreeItem, tag,
                    Optional.of(contactData),
                    p -> Optional.of(contactData.getData()));

            addSubNode(contactNode, "Komentarz", p -> Optional.ofNullable(contactData.getComment()));
        }

    }

    void addStatusChanges(TreeItem<PersonTreeItem> root) {
        final TreeItem<PersonTreeItem> statusesTreeItem =
                addSubNode(root, "Historia statusów",
                        p -> Optional.ofNullable(p.getStatusChanges()).map(statusChanges ->
                                "(" + statusChanges.size() + ")"));
        statusesTreeItem.setExpanded(true);

        final List<PersonStatusChange> statusChanges = emptyIfNull(person.getStatusChanges());

        for (final PersonStatusChange statusChange : statusChanges) {
            final String tag = statusChange.getEventType().getName();

            final TreeItem<PersonTreeItem> statusTreeItem = addSubNode(statusesTreeItem, tag,
                    Optional.of(statusChange),
                    p -> Optional.of(statusChange).map(PersonStatusChange::getWhen)
                            .map(LocalDate::toString));

            addSubNode(statusTreeItem, "Oryginalna wartość",
                    p -> Optional.ofNullable(statusChange.getOriginalValue()));
        }

    }

    void addBankAccounts(TreeItem<PersonTreeItem> root) {
        final List<BankAccount> bankAccounts = emptyIfNull(person.getBankAccounts());

        final TreeItem<PersonTreeItem> bankAccountsTreeItem =
                addSubNode(root, "Konta bankowe",
                        p -> Optional.of("(" + bankAccounts.size() + ")"));
        bankAccountsTreeItem.setExpanded(true);

        for (BankAccount bankAccount : bankAccounts) {
            final TreeItem<PersonTreeItem> treeItem = addSubNode(bankAccountsTreeItem, "Konto",
                    Optional.of(bankAccount),
                    p -> Optional.of(bankAccount).map(BankAccount::getNumber));

            addSubNode(treeItem, "Komentarz",
                    p -> Optional.ofNullable(bankAccount.getNotes()));
        }
    }

    void addPesel(TreeItem<PersonTreeItem> rootNode) {
        if (StringUtils.isNotEmpty(person.getPesel()) || expand) {
            addSubNode(rootNode, "Pesel", p -> Optional.ofNullable(trimToNull(p.getPesel())));
        }
    }

    void addIdNumber(TreeItem<PersonTreeItem> rootNode) {
        if (StringUtils.isNotEmpty(person.getIdNumber()) || expand) {
            addSubNode(rootNode, "Numer dowodu", p -> Optional.ofNullable(trimToNull(p.getIdNumber())));
        }
    }


}
