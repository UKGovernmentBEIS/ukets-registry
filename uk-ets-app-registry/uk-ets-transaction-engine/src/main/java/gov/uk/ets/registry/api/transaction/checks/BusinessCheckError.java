package gov.uk.ets.registry.api.transaction.checks;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import lombok.ToString;

/**
 * Represents a business check error.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"code"})
public class BusinessCheckError implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -8004571123486509345L;

    /**
     * The code.
     */
    private Integer code;

    /**
     * The message.
     */
    private String message;

}
