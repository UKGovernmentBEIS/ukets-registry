package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AccountDetailsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountDetailsReportService implements ReportTypeService<AccountDetailsReportData> {

    private final ReportDataMapper<AccountDetailsReportData> mapper;

    @Override
    public List<AccountDetailsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(AccountDetailsReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getCompliantEntity().getRegulator());
        data.add(reportData.getAccountHolder().getId());
        data.add(reportData.getAccountHolder().getName());

        data.add(reportData.getAccount().getNumber());
        data.add(reportData.getAccount().getPublicIdentifier());
        data.add(reportData.getAccount().getName());
        data.add(reportData.getAccount().getType());
        data.add(reportData.getAccount().getStatus());
        data.add(reportData.getAccount().getOpeningDate());
        data.add(reportData.getAccount().getClosingDate());

        data.add(reportData.getAccount().getComplianceStatus());

        data.add(reportData.getAccount().getBalance());
        data.add(reportData.getAccount().getNumberOfARs());
        data.add(reportData.getAccount().getApprovalSecondARRequired());
        data.add(reportData.getAccount().getSinglePersonApprovalRequired());
        data.add(reportData.getAccount().getTransfersOutsideTal());

        data.add(reportData.getAccountHolder().getCompanyRegistrationNumber());
        data.add(reportData.getAccountHolder().getReasonForNoRegistrationNumber());

        data.add(reportData.getAccountHolder().getContact().getAddressLine1());
        data.add(reportData.getAccountHolder().getContact().getAddressLine2());
        data.add(reportData.getAccountHolder().getContact().getAddressLine3());
        data.add(reportData.getAccountHolder().getContact().getTownOrCity());
        data.add(reportData.getAccountHolder().getContact().getStateOrProvince());
        data.add(reportData.getAccountHolder().getContact().getCountry());
        data.add(reportData.getAccountHolder().getContact().getPostCode());

        data.add(reportData.getAccountHolder().getPrimaryAr().getFirstName());
        data.add(reportData.getAccountHolder().getPrimaryAr().getLastName());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getAddressLine1());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getAddressLine2());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getAddressLine3());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getTownOrCity());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getStateOrProvince());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getCountry());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getPhoneNumber1());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getPhoneNumber2());
        data.add(reportData.getAccountHolder().getPrimaryAr().getContact().getEmail());

        data.add(reportData.getAccountHolder().getAltAr().getFirstName());
        data.add(reportData.getAccountHolder().getAltAr().getLastName());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getAddressLine1());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getAddressLine2());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getAddressLine3());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getTownOrCity());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getStateOrProvince());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getCountry());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getPhoneNumber1());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getPhoneNumber2());
        data.add(reportData.getAccountHolder().getAltAr().getContact().getEmail());
        
        data.add(reportData.getAccount().isExcludedFromBilling());
        data.add(reportData.getAccount().getExcludedFromBillingRemarks());

        // billing address
        data.add(reportData.getAccount().getContact().getAddressLine1());
        data.add(reportData.getAccount().getContact().getAddressLine2());
        data.add(reportData.getAccount().getContact().getAddressLine3());
        data.add(reportData.getAccount().getContact().getTownOrCity());
        data.add(reportData.getAccount().getContact().getStateOrProvince());
        data.add(reportData.getAccount().getContact().getCountry());
        data.add(reportData.getAccount().getContact().getPostCode());
        data.add(reportData.getAccount().getContact().getContactName());
        data.add(reportData.getAccount().getContact().getPhoneNumber1());
        data.add(reportData.getAccount().getContact().getEmail());
        data.add(reportData.getAccount().getContact().getSopCustomerId());

        data.add(reportData.getCompliantEntity().getInstallationId());
        data.add(reportData.getCompliantEntity().getInstallationName());
        data.add(reportData.getCompliantEntity().getInstallationActivity());
        data.add(reportData.getCompliantEntity().getInstallationPermitId());
        data.add(reportData.getCompliantEntity().getAircraftOperatorId());
        data.add(reportData.getCompliantEntity().getAircraftMonitoringPlanId());

        data.add(reportData.getCompliantEntity().getMaritimeOperatorId());
        data.add(reportData.getCompliantEntity().getImo());
        data.add(reportData.getCompliantEntity().getMaritimeMonitoringPlanId());

        data.add(reportData.getCompliantEntity().getEmitterId());

        data.add(reportData.getCompliantEntity().getFirstYearOfVerifiedEmissionSubmission());
        data.add(reportData.getCompliantEntity().getLastYearOfVerifiedEmissionSubmission());
        //Mets Contacts
        data.add(reportData.getAccount().getMetsContacts());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(

                "Regulator",
                "AH ID",
                "AH name",

                "Account number",
                "PID",

                "Account name",
                "Account type",
                "Account status",
                "Account opening date (UTC)",
                "Account closing date (UTC)",

                "Compliance status",

                "Available quantity (balance)",
                "Number of ARs",
                "Rule - second AR approval required for transfers",
                "Rule – second AR approval required for surrenders",
                "Rule - allow transfers outside TAL",

                "AH Company registration number",
                "AH reason for no registration number",

                "AH Address line 1",
                "AH Address line 2",
                "AH Address line 3",
                "AH town or city",
                "AH state or province",
                "AH country",
                "AH postal code or zip",

                "AH first name (PC)",
                "AH last name (PC)",
                "AH work address 1 (PC)",
                "AH work address 2 (PC)",
                "AH work address 3 (PC)",
                "AH work town or city (PC)",
                "AH work state or province (PC)",
                "AH work country (PC)",
                "AH work phone 1 (PC)",
                "AH work phone 2 (PC)",
                "AH work email (PC)",

                "AH first name (APC)",
                "AH last name (APC)",
                "AH work address 1 (APC)",
                "AH work address 2 (APC)",
                "AH work address 3 (APC)",
                "AH work town or city (APC)",
                "AH work state or province (APC)",
                "AH work country (APC)",
                "AH work phone 1 (APC)",
                "AH work phone 2 (APC)",
                "AH work email (APC)",
                
                "Excluded From Billing",
                "Exclusion Remarks",

                "Billing address 1",
                "Billing address 2",
                "Billing address 3",
                "Billing town or city",
                "Billing state or province",
                "Billing country",
                "Billing address postal code or zip",
                "Billing contact name",
                "Billing phone number",
                "Billing email",
                "SOP Customer ID",

                "Installation ID",
                "Installation name",
                "Installation activity",
                "Installation permit ID",
                "Aircraft operator ID",
                "Aviation EMP ID",
                "Maritime operator ID",
                "Company IMO number",
                "Maritime EMP ID",
                "Emitter ID",
                "First year of verified emission submission",
                "Last year of verified emission submission",
                "METS Contacts"
            );
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0008;
    }
}
