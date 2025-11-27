package gov.uk.ets.registry.api.integration.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AccountOpeningEvent {

    private AccountDetailsMessage accountDetails;
    private AccountHolderMessage accountHolder;
}
