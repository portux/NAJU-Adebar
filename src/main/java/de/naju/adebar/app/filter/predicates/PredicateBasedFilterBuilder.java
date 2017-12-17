package de.naju.adebar.app.filter.predicates;

import org.springframework.util.Assert;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import de.naju.adebar.app.filter.AbstractFilterBuilder;

/**
 * An {@link AbstractFilterBuilder} which consumes and produces a {@link Predicate}
 * 
 * @author Rico Bergmann
 * @see AbstractPredicateBasedFilter
 */
public class PredicateBasedFilterBuilder<T>
    implements AbstractFilterBuilder<BooleanBuilder, AbstractPredicateBasedFilter<T>> {
  private BooleanBuilder predicate;

  public PredicateBasedFilterBuilder() {
    this.predicate = new BooleanBuilder();
  }

  @Override
  public AbstractFilterBuilder<BooleanBuilder, AbstractPredicateBasedFilter<T>> applyFilter(
      AbstractPredicateBasedFilter<T> filter) {
    Assert.notNull(filter, "Filter may not be null");
    this.predicate = filter.filter(predicate);
    return this;
  }

  @Override
  public BooleanBuilder filter() {
    return predicate;
  }

}