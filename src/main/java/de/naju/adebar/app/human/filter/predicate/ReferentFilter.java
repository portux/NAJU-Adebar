package de.naju.adebar.app.human.filter.predicate;

import java.util.List;
import com.querydsl.core.BooleanBuilder;
import de.naju.adebar.app.filter.FilterType;
import de.naju.adebar.model.human.QPerson;
import de.naju.adebar.model.human.Qualification;

public class ReferentFilter implements PersonFilter {

  private final QPerson person = QPerson.person;
  private PersonFilter filterImpl;

  public ReferentFilter(FilterType filterType) {
    this.filterImpl = new GeneralReferentFilter(filterType);
  }

  public ReferentFilter(List<Qualification> qualifications) {
    this.filterImpl = new QualificationFilter(qualifications);
  }

  @Override
  public BooleanBuilder filter(BooleanBuilder input) {
    return filterImpl.filter(input);
  }

  private class GeneralReferentFilter implements PersonFilter {
    private FilterType filterType;

    public GeneralReferentFilter(FilterType filterType) {
      this.filterType = filterType;
    }

    @Override
    public BooleanBuilder filter(BooleanBuilder input) {
      switch (filterType) {
        case ENFORCE:
          return input.and(person.referent.isTrue());
        case IGNORE:
          return input.and(person.referent.isFalse());
        default:
          throw new AssertionError(filterImpl);
      }
    }
  }

  private class QualificationFilter implements PersonFilter {
    private List<Qualification> qualifications;

    public QualificationFilter(List<Qualification> qualifications) {
      this.qualifications = qualifications;
    }

    @Override
    public BooleanBuilder filter(BooleanBuilder input) {
      qualifications.forEach(qualification -> input.and(
          person.referentProfile.qualifications.contains(qualification.getName(), qualification)));
      return input;
    }
  }

}