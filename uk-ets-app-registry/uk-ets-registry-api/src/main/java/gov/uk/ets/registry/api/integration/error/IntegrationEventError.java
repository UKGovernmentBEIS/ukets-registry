package gov.uk.ets.registry.api.integration.error;

import static gov.uk.ets.registry.api.integration.error.ContactPoint.*;

import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum IntegrationEventError {

    // Account Opening
    ERROR_0100("0100","Internal Server Error", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0101("0101","Data not in expected format", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0102("0102","Emitter ID already exists in Registry", registryAdministrators(), metsRegulators()),
    ERROR_0103("0103","Mandatory field is not provided", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0104("0104","CRN or CRN Justification field not provided", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0105("0105","Permit/EMP ID already exists in Registry", registryAdministrators(), metsRegulators()),
    ERROR_0106("0106","FYVE value is before 2026", registryMets(), List.of()),
    ERROR_0107("0107","Country Code does not exist", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0108("0108","Regulator value does not exist", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0109("0109","Account Holder Type does not exist", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0110("0110","Company IMO number already exists in Registry", registryAdministrators(), metsRegulators()),
    ERROR_0111("0111","FYVE value is before 2021", registryMets(), List.of()),

    // Operator
    ERROR_0200("0200","Internal Server Error", serviceDeskAndTU(), registryDesnz()),
    ERROR_0201("0201","Data validation error", serviceDeskAndTU(), registryMets()),
    ERROR_0202("0202","Emitter ID does not exist in METS", registryAdministrators(), metsRegulators()),
    ERROR_0203("0203","Emitter ID is already associated with an Operator ID", metsRegulators(), registryAdministrators()),
    ERROR_0204("0204","Operator ID already exists ", metsRegulators(), registryAdministrators()),
    ERROR_0205("0205","Invalid Emitter ID", List.of(), List.of()),

    // Emissions
    ERROR_0800("0800","Internal Server Error", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0801("0801","Operator ID is mandatory.", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0802("0802","Year is mandatory", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0803("0803","Operator ID does not exist in the Registry.", registryMets(), List.of()),
    ERROR_0805("0805","The Operator ID is associated with an Account with status Closed.", registryAdministrators(), metsRegulators()),
    ERROR_0806("0806","The Operator ID is associated with an Account with status Transfer Pending.", registryAdministrators(), metsRegulators()),
    ERROR_0807("0807","The Operator ID is associated with an Account with status Closure Pending.", registryAdministrators(), metsRegulators()),
    ERROR_0808("0808","The Account is marked as EXCLUDED for the year provided", registryAdministrators(), metsRegulators()),
    ERROR_0809("0809","The emissions value must be a non-negative Integer.", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0811("0811","The Year must be greater or equal to 2021.", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0812("0812","The Year must not be equal to the current year.", registryAdministrators(), metsRegulators()),
    ERROR_0813("0813","The Year must not be before the First Year of Verified Emissions (FYVE).", registryAdministrators(), metsRegulators()),
    ERROR_0814("0814","The Year must not be after the Last Year of Verified Emissions (LYVE).", registryAdministrators(), metsRegulators()),
    ERROR_0815("0815","The Year must not be equal to a future year.", registryServiceDeskAndTuSupport(), desnz()),
    ERROR_0816("0816","The Year must be greater or equal to 2026.", registryServiceDeskAndTuSupport(), desnz());

    private final String code;
    private final String message;
    private final List<ContactPoint> actionRecipients;
    private final List<ContactPoint> informationRecipients;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<ContactPoint> getActionRecipients() {
        return actionRecipients;
    }

    public List<ContactPoint> getInformationRecipients() {
        return informationRecipients;
    }

    public boolean isActionFor(ContactPoint contactPoint) {
        return actionRecipients.contains(contactPoint);
    }

    public boolean isInfoFor(ContactPoint contactPoint) {
        return informationRecipients.contains(contactPoint);
    }

    public boolean isRelatedFor(ContactPoint contactPoint) {
        return isActionFor(contactPoint) || isInfoFor(contactPoint);
    }

    @Override
    public String toString() {
        return name() + ": " + message;
    }
}
