package gov.uk.ets.registry.api.common.search;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.StringPath;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class SearchUtils {

  private SearchUtils() {}

  /**
   * Creates and returns the from date {@link BooleanExpression} corresponding to the input date
   * @param input The {@link Date}
   * @param path The {@link DateTimePath<Date>}
   * @return
   */
  public static BooleanExpression getFromDatePredicate(Date input, DateTimePath<Date> path) {
    LocalDate localDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    return path.eq(date).or(path.after(date));
  }

  /**
   * Creates and returns the until date {@link BooleanExpression} corresponding to the input date
   * @param input The {@link Date}
   * @param path The {@link DateTimePath<Date>}
   * @return
   */
  public static BooleanExpression getUntilDatePredicate(Date input, DateTimePath<Date> path) {
    LocalDate localDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Date date = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    return path.before(date);
  }

  /**
   * Creates and returns the {@link BooleanExpression} corresponding to the concatenation of first
   * and last name.
   *
   * @param term      The search by name term value
   * @param firstName The first name {@link StringPath}
   * @param lastName  The last name column {@link StringPath}
   * @return The {@link BooleanExpression} corresponding to the concatenation of first and last
   * name.
   */
  public static BooleanExpression getFirstNameLastNamePredicate(String term, StringPath firstName,
      StringPath lastName) {
    if (term == null) {
      throw new IllegalArgumentException("The criterion value should not be null");
    }
    String space = " ";
    return firstName.concat(space).concat(lastName).containsIgnoreCase(term.trim());
  }
}
