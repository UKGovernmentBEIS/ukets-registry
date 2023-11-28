package uk.gov.ets.transaction.log.domain.type;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * Enumerates the various commitment periods.
 */
public enum CommitmentPeriod {

  /**
   * Commitment period zero (01/01/2005 - 31/12/2007).
   */
  CP0(0, LocalDateTime.of(2005, Month.JANUARY, 1, 00, 00, 01),
      LocalDateTime.of(2007, Month.DECEMBER, 31, 23, 59, 59)),

  /**
   * Commitment period one (01/01/2008 - 31/12/2012).
   */
  CP1(1, LocalDateTime.of(2008, Month.JANUARY, 1, 00, 00, 01),
      LocalDateTime.of(2012, Month.DECEMBER, 31, 23, 59, 59)),

  /**
   * Commitment period two (01/01/2013 - 31/12/2020).
   */
  CP2(2, LocalDateTime.of(2013, Month.JANUARY, 1, 00, 00, 01),
      LocalDateTime.of(2020, Month.DECEMBER, 31, 23, 59, 59)),

  /**
   * Commitment period three (01/01/2021 - 31/12/2030).
   */
  CP3(3, LocalDateTime.of(2021, Month.JANUARY, 1, 00, 00, 01),
      LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59, 59)),
  ;

  /**
   * The code.
   */
  private int code;

  /**
   * The start date.
   */
  private LocalDateTime start;

  /**
   * The end date.
   */
  private LocalDateTime end;

  /**
   * Constructor.
   *
   * @param code  The code.
   * @param start The start date.
   * @param end   The end date.
   */
  CommitmentPeriod(int code, LocalDateTime start, LocalDateTime end) {
    this.code = code;
    this.start = start;
    this.end = end;
  }

  /**
   * Returns the code.
   *
   * @return the code.
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns the start date.
   *
   * @return the start date.
   */
  public LocalDateTime getStart() {
    return start;
  }

  /**
   * Returns the end date.
   *
   * @return the end date.
   */
  public LocalDateTime getEnd() {
    return end;
  }

    /**
     * Returns the current commitment period.
     *
     * @return the current commitment period.
     */
    public static CommitmentPeriod getCurrentPeriod() {
        CommitmentPeriod result = null;
        LocalDateTime now = LocalDateTime.now();
        for (CommitmentPeriod element : CommitmentPeriod.values()) {
            if (now.isAfter(element.getStart()) && now.isBefore(element.getEnd())) {
                result = element;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the commitment period based on the input code.
     * @param code The code.
     * @return a commitment period.
     */
    public static CommitmentPeriod findByCode(int code) {
        for (CommitmentPeriod period : values()) {
            if (period.code == code) {
                return period;
            }
        }
        return null;
    }

}
