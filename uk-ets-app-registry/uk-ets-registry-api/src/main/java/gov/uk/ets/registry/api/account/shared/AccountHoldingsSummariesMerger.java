package gov.uk.ets.registry.api.account.shared;

import java.util.ArrayList;
import java.util.List;

import gov.uk.ets.registry.api.account.web.model.AccountHoldingsSummaryDTO;
import lombok.Builder;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * This class contains the logic to merge to lists with available and reserved
 * unit block quantities to a single result.
 * 
 * @author fragkise
 *
 */
@Builder
public class AccountHoldingsSummariesMerger {

	private List<AccountHoldingsSummaryDTO> available;
	private List<AccountHoldingsSummaryDTO> reserved;

	/**
	 * Merges the two lists into one.
	 * The resulting list is sorted by unit type, isSOP flag, originalCommitmentPeriod (desc) & applicableCommitmentPeriod (desc).
	 * 
	 * @return
	 */
	public List<AccountHoldingsSummaryDTO> merge() {

		List<AccountHoldingsSummaryDTO> holdings = new ArrayList<>(available);

		/* mergeAvailableAndReservedResults */
		for (AccountHoldingsSummaryDTO entry : reserved) {
			if (holdings.contains(entry)) {
				holdings.get(holdings.indexOf(entry)).setReservedQuantity(entry.getReservedQuantity());
			} else {
				holdings.add(entry);
			}
		}

		return holdings.stream().
			sorted(comparing(AccountHoldingsSummaryDTO::getType)
				.thenComparing(comparing(AccountHoldingsSummaryDTO::getOriginalPeriod).reversed())
				.thenComparing(comparing(AccountHoldingsSummaryDTO::getApplicablePeriod).reversed())
				.thenComparing(comparing(AccountHoldingsSummaryDTO::getSubjectToSop))).
				collect(toList());
	}
}
