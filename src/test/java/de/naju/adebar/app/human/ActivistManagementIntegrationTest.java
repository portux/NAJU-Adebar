package de.naju.adebar.app.human;

import de.naju.adebar.model.human.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Basic behavior testing for the {@link PersistentPersonManager} and its behavior on
 * {@link ActivistProfile} instances
 * 
 * @author Rico Bergmann
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback
@Component
public class ActivistManagementIntegrationTest {
  @Autowired
  private PersonFactory personFactory;
  @Autowired
  private PersonManager personManager;
  @Autowired
  private PersonRepository personRepo;
  @Autowired
  private ActivistProfileRepository activistRepo;
  private Person claus;

  @Before
  public void setUp() {
    Address clausAddress = new Address("Hinner der Boje 7", "24103", "Auf'm Meer");
    this.claus = personFactory.buildNew("Claus", "Störtebecker", "der_kaeptn@web.de").makeActivist()
        .create();
    claus.setAddress(clausAddress);

    personManager.savePerson(claus);
  }

  @Test
  public void testSaveActivist() {
    Assert.assertTrue(claus.toString() + " should have been saved!",
        activistRepo.exists(claus.getId()));
  }

  @Test
  public void testUpdateActivist() {

    // update the person
    claus = personManager.repository().findEntry(claus.getId());
    claus.getActivistProfile().setJuleicaCard(new JuleicaCard());

    // save the updated person
    claus = personManager.updatePerson(claus.getId(), claus);

    Assert.assertTrue("Person should have Juleica card", claus.getActivistProfile().hasJuleica());
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void testRemoveActivist() {

    // we need to disable propagation in order to enforce the data to be
    // deleted

    claus.setActivistProfile(null);
    personManager.updatePerson(claus.getId(), claus);

    Assert.assertFalse("Entry should have been removed: " + activistRepo.findOne(claus.getId()),
        activistRepo.exists(claus.getId()));

    // clean up
    personRepo.delete(claus.getId());
  }
}
