package gov.uk.ets.registry.api.transaction.domain.data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SignatureInfo implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 8199413408654994203L;

    /**
     * The signature given by the signing-api during signing.
     */
    private String signature;

    /**
     * Raw signed-data, a string representation of a JSON document.
     */
    private String data;
}
