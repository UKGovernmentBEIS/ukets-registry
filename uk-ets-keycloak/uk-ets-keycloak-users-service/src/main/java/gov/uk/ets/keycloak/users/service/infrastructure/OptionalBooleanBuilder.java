package gov.uk.ets.keycloak.users.service.infrastructure;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.function.Function;

/**
 * Helper class for filtering by optional criteria values.
 */
public class OptionalBooleanBuilder {

  private final BooleanExpression predicate;

  /**
   * @param predicate The initial predicate of builder. It must not be null
   */
  public OptionalBooleanBuilder(BooleanExpression predicate) {
    this.predicate = predicate;
  }

  /**
   * Returns an {@link OptionalBooleanBuilder} which predicate is the intersection of this builder
   * predicate and the returned expression of expressionFunction when the value applied to it
   *
   * @param expressionFunction The function which returns the boolean expression
   * @param value              The parameter of function applied
   * @param <T>                The type of value
   * @return The new {@link OptionalBooleanBuilder}
   */
  public <T> OptionalBooleanBuilder notNullAnd(Function<T, BooleanExpression> expressionFunction,
      T value) {
    if (value != null) {
      return new OptionalBooleanBuilder(predicate.and(expressionFunction.apply(value)));
    }
    return this;
  }

  /**
   * @return The predicate of builder;
   */
  public BooleanExpression build() {
    return predicate;
  }
}
