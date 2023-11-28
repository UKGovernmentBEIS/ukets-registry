package gov.uk.ets.reports.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountHolder {
    private Long id;
    private String name;
    private String companyRegistrationNumber;
    private String reasonForNoRegistrationNumber;
    private Contact contact;
    private String firstName;
    private String lastName;
    private AccountHolderRepresentative primaryAr;
    private AccountHolderRepresentative altAr;

}
