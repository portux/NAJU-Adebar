package de.naju.adebar.model.human;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Basic testing of the {@link PersistentPersonManager}
 * @author Rico Bergmann
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Component
public class PersonManagerIntegrationTest {
    @Autowired private PersonRepository personRepo;
    @Autowired private PersistentPersonManager personManager;
    private PersonId bertaId;
    private Person claus, berta;
    private Address bertaAddress, heinzAddress;
    private LocalDate heinzDob;

    @Before public void setUp() {
        PersonId clausId = new PersonId();
        Address clausAddress = new Address("Hinner der Boje 7", "24103", "Auf'm Meer");
        LocalDate clausDob = LocalDate.now().minusYears(42L);
        this.claus = new Person(clausId, "Claus", "Störtebecker", "der_kaeptn@web.de",
                Gender.MALE, clausAddress, clausDob, new NabuMembership());

        this.bertaId = new PersonId();
        this.bertaAddress = new Address("An der Schiefen Ebene 2", "01234", "Entenhausen", "Zimmer 13");
        LocalDate bertaDob = LocalDate.now().minusYears(27L);
        this.berta = new Person(bertaId, "Berta", "Beate", "berta@gmx.net",
                Gender.FEMALE, bertaAddress, bertaDob, new NabuMembership());

        this.heinzAddress = new Address("Zu Hause", "98765", "Teudschland");
        this.heinzDob = LocalDate.now().minusYears(3);
    }

    @Test public void testSavePerson() {
        claus = personManager.savePerson(claus);
        berta = personManager.savePerson(berta);

        Assert.assertTrue(claus.toString() + " should have been saved!", personRepo.exists(claus.getId()));
        Assert.assertTrue(berta.toString() + " should have been saved!", personRepo.exists(bertaId));
    }

    @Test public void testCreatePerson() {
        Person heinz = personManager.createPerson("Heinz", "Der Heinz", "heeeiiiinnnzzzzz@gmail.com",
                Gender.OTHER, heinzAddress, heinzDob);
        Assert.assertTrue(heinz.toString() + " should have been saved!", personRepo.exists(heinz.getId()));
    }

    @Test public void testUpdatePerson() {
        claus = personManager.savePerson(claus);
        claus.setAddress(bertaAddress);
        claus.setDateOfBirth(heinzDob);
        claus = personManager.updatePerson(claus.getId(), claus);
        Assert.assertEquals("Address should have been updated!", bertaAddress, claus.getAddress());
        Assert.assertEquals("Dob should have been updated!", heinzDob, claus.getDateOfBirth());
    }

}
