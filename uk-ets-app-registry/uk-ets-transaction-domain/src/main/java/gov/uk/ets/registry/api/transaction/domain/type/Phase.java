package gov.uk.ets.registry.api.transaction.domain.type;

import java.time.LocalDateTime;
import java.time.Month;

import lombok.Getter;

/**
 * Phase is a similar concept with Commitment Period from Kyoto Protocol.
 * 
 * @author P35036
 *
 */
@Getter
public enum Phase {

	/**
	 * Phase one (01/01/2021 - 31/12/2030).
	 */
	PHASE_1(LocalDateTime.of(2021, Month.JANUARY, 1, 00, 00, 01),
	        LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59, 59));

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
	 * @param code
	 *            The code.
	 * @param start
	 *            The start date.
	 * @param end
	 *            The end date.
	 */
	Phase(LocalDateTime start, LocalDateTime end) {
		this.start = start;
		this.end = end;
	}
	
	public boolean isYearWithinPhase(int year) {
		return start.getYear() <= year && year <= end.getYear();
	}
}
