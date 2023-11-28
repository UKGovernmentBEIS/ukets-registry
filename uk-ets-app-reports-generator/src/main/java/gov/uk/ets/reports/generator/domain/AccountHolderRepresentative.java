package gov.uk.ets.reports.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountHolderRepresentative {
    private String firstName;
    private String lastName;
    private Contact contact;
}
