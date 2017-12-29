package de.naju.adebar.model.human;

import java.util.Collection;
import de.naju.adebar.model.ChangeSetEntry;
import de.naju.adebar.model.EntityUpdatedEvent;

/**
 * Base for all events which are raised by the {@link Person} aggregate
 *
 * @author Rico Bergmann
 *
 */
public abstract class AbstractPersonRelatedEvent extends EntityUpdatedEvent<Person> {


  /**
   * @see EntityUpdatedEvent#EntityUpdatedEvent(E)
   */
  protected AbstractPersonRelatedEvent(Person person) {
    super(person);
  }

  /**
   * @see EntityUpdatedEvent#EntityUpdatedEvent(E, Collection)
   */
  protected AbstractPersonRelatedEvent(Person person, Collection<ChangeSetEntry> changeset) {
    super(person, changeset);
  }
}
