package gov.uk.ets.registry.api.ar.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Data transfer object which represents an Authorized Representative Update Action
 */
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ARUpdateActionDTO {
   /**
    * The unique business identifier ot the account related with this action.
    */
   private Long accountIdentifier;
   /**
    * The type of the update action.
    */
   private ARUpdateActionType updateType;
   /**
    * The {@link AuthorizedRepresentativeDTO} AR candidate.
    */
   private AuthorizedRepresentativeDTO candidate;
   /**
    * The {@link AuthorizedRepresentativeDTO} AR that should be replaced by the AR candidate.
    */
   private AuthorizedRepresentativeDTO replacee;
   /**
    * The comment registered when restoring or suspending an AR from an account
    */
   private String comment;
}
