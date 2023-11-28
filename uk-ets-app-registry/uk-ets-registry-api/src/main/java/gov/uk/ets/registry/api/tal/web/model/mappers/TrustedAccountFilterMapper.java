package gov.uk.ets.registry.api.tal.web.model.mappers;

import gov.uk.ets.registry.api.tal.domain.TrustedAccountFilter;
import gov.uk.ets.registry.api.tal.web.model.search.TALSearchCriteria;

/**
 * Mapper which is responsible for translating the {@link TALSearchCriteria} web client input to
 * the {@link TrustedAccountFilter} value object.
 */
public class TrustedAccountFilterMapper {
    /**
     * Maps a {@link TALSearchCriteria} to a {@link TrustedAccountFilter}.
     *
     * @param criteria The web client input
     * @return The {@link TrustedAccountFilter} value object
     */
    public TrustedAccountFilter map(TALSearchCriteria criteria) {
        return TrustedAccountFilter
                .builder()
                .accountFullIdentifier(criteria.getAccountNumber())
                .name(criteria.getAccountNameOrDescription())
                .underSameAccountHolder(criteria.getTrustedAccountType())
                .identifier(criteria.getAccountId())
                .build();
    }

}
