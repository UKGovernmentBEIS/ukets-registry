package uk.gov.ets.transaction.log.messaging.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an answer sent to an incoming account opening request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpeningAnswer {

    /**
     * The old account identifier (if exists).
     */
    private Long oldIdentifier;

    /**
     * The account identifier.
     */
    private Long identifier;

    /**
     * The account full identifier.
     */
    private String fullIdentifier;

}
