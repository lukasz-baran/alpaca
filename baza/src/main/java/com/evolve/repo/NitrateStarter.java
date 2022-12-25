package com.evolve.repo;

import com.evolve.domain.Address;
import com.evolve.domain.Person;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.mapper.JacksonMapperModule;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;

import java.util.Date;
import java.util.List;

import static org.dizitart.no2.collection.Document.createDocument;

@Slf4j
public class NitrateStarter {

    public void starter() {
        log.info("start - nitrate");
        try (Nitrite db = Nitrite.builder()
                .loadModule(new JacksonMapperModule()).openOrCreate()) {
            NitriteCollection collection = db.getCollection("test");

            // podejście oparte o dokumenty - tego nie chcemy w aplikacji

            // create a document to populate data
            Document doc = createDocument("firstName", "John")
                    .put("lastName", "Doe")
                    .put("birthDay", new Date())
                    .put("data", new byte[] {1, 2, 3})
                    .put("fruits", List.of("apple", "orange"))
                    .put("note", "a quick brown fox jump over the lazy dog");

            // insert the document
            collection.insert(doc);

            // update the document
            //collection.update(eq("firstName", "John"), createDocument("lastName", "Wick"));

            DocumentCursor cursor = collection.find();
            for (Document document : cursor) {
                System.out.println(document);
            }


            // create an object repository
            ObjectRepository<Person> personRepo = db.getRepository(Person.class);
            Person testPerson = Person.builder()
                    .firstName("Łukasz")
                    .addresses(List.of(new Person.PersonAddress("Tkaczowa", "code", "Boguchwała", Person.AddressType.HOME)))
                    .build();

            personRepo.insert(testPerson);
            Cursor<Person> personCursor = personRepo.find();
            for (Person document : personCursor) {
                System.out.println(document);
            }

            ObjectRepository<Address> addressRepo = db.getRepository(Address.class);
            addressRepo.insert(Address.of("Tkaczowa", "code"));
            Cursor<Address> addressCursor = addressRepo.find();
            for (Address document : addressCursor) {
                System.out.println(document);
            }


        }

    }

}
