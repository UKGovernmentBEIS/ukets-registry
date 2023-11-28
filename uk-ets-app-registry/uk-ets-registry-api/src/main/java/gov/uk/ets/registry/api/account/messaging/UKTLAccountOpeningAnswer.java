package gov.uk.ets.registry.api.account.messaging;

import java.io.Serializable;
import lombok.Data;

/**
 * account-opening.answer.topic response from UK Transaction log
 */
@Data
public class UKTLAccountOpeningAnswer implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 999664470158465784L;

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
