package gov.uk.ets.registry.api.account.service.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.BillingContactDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.SalesContactDetailsDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.service.UserService;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class AccountOpeningPdfGenerator {

    private final UserService userService;

    private final Mapper mapper;

    private final DocumentHelper helper;

    private final HeaderAndFooterPageEventHelper headerAndFooterPageEventHelper;

    public byte[] generate(Task task) {

        Document document = new Document(PageSize.A4, 0, 0, 70, 70);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            // Current data may contain updates for AH or Regulator
            AccountDTO accountDTO = mapper.convertToPojo(task.getDifference(), AccountDTO.class);

            PdfWriter writer = PdfWriter.getInstance(document, stream);
            writer.setPageEvent(headerAndFooterPageEventHelper);
            document.open();
            document.addAuthor("");
            document.addTitle("Open account request");

            firstPagePadding(document);

            createGenericInfoSection(document, task);

            createAccountHolderSection(document, accountDTO);

            createAccountDetailsSection(document, accountDTO);

            createTransactionRulesSection(document, accountDTO);

            createOperatorSection(document, accountDTO);

            createAuthorisedRepresentativesSection(document, accountDTO);


        } catch (DocumentException ex) {
            log.error("There was an error in generating the PDF document");
        }
        document.close();

        return stream.toByteArray();
    }

    private void firstPagePadding(Document document) {
        if (document.getPageNumber() == 0) {
            PdfPTable tablePadding = new PdfPTable(1);
            tablePadding.setTotalWidth(DocumentHelper.CONTENT_WIDTH);
            tablePadding.setWidths(new int[]{DocumentHelper.CONTENT_WIDTH});
            tablePadding.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell topLineCell = new PdfPCell(new Paragraph(""));
            topLineCell.setBorder(Rectangle.NO_BORDER);
            topLineCell.setFixedHeight(13);
            tablePadding.addCell(topLineCell);
            document.add(tablePadding);
        }
    }

    private void createAccountHolderSection(Document document, AccountDTO accountDTO) {
        createSectionTitle(document,"1. Account Holder", 16);

        createHorizontalLine(document);

        Long oldAhId = Optional.of(accountDTO).map(AccountDTO::getOldAccountHolder).map(AccountHolderDTO::getId).orElse(null);
        boolean isAccountHolderChanged = !Objects.equals(oldAhId, accountDTO.getChangedAccountHolderId());

        boolean isOrganisation = "ORGANISATION".equals(accountDTO.getAccountHolder().getType().name());

        if (isAccountHolderChanged) {
            createChangedLabelRow(document);
            createHorizontalLine(document);
        }

        if (isOrganisation) {
            createOrganisationSection(document, accountDTO, isAccountHolderChanged);
        } else {
            createIndividualSection(document, accountDTO, isAccountHolderChanged);
        }

        createPublicContactsDetails(document, accountDTO, isAccountHolderChanged);

        PdfPTable table = createTable(false);
        addRowSeparator(table, 2);
        document.add(table);
    }

    private void createIndividualSection(Document document, AccountDTO accountDTO, boolean isAccountHolderChanged) {
        PdfPTable table = createTable(isAccountHolderChanged);
        createSectionTitle(document, "Individual details", 14);

        addRowSeparator(table, 1);

        DetailsDTO details = accountDTO.getAccountHolder().getDetails();;
        DetailsDTO changedDetails = null;
        if(isAccountHolderChanged){
            details = Optional.ofNullable(accountDTO.getOldAccountHolder()).map(AccountHolderDTO::getDetails).orElse(null);
            changedDetails = accountDTO.getAccountHolder().getDetails();
        }

        checkValuesAndAddRow(table, "First and middle names", details, changedDetails, DetailsDTO::getFirstName);
        checkValuesAndAddRow(table, "Last name", details, changedDetails, DetailsDTO::getLastName);
        checkValuesAndAddRow(table, "Country of birth",details, changedDetails,
            dto -> Optional.ofNullable(dto.getBirthCountry()).map(this::getCountryLabel).orElse(null));
        addRow(table, "I confirm that the account holder is aged 18 or over", "Yes", null);

        addRowSeparator(table, 2);

        document.add(table);


        createSectionTitle(document, "Individual's contact details", 14);

        table = createTable(isAccountHolderChanged);

        AddressDTO address = accountDTO.getAccountHolder().getAddress();
        AddressDTO changedAddress =
            Optional.ofNullable(accountDTO.getOldAccountHolder()).map(AccountHolderDTO::getAddress).orElse(null);
        addAddressSection(table, address, changedAddress, false);

        PhoneNumberDTO phoneNumber = accountDTO.getAccountHolder().getPhoneNumber();
        PhoneNumberDTO changedPhoneNumber =
            Optional.ofNullable(accountDTO.getOldAccountHolder()).map(AccountHolderDTO::getPhoneNumber).orElse(null);
        addPhoneNumber(table, phoneNumber, changedPhoneNumber, false);

        addRow(table, "Email", accountDTO.getAccountHolder().getEmailAddress().getEmailAddress(), null);

        addRowSeparator(table, 2);

        document.add(table);
    }

    private void addAddressSection(PdfPTable table, AddressDTO address, AddressDTO changedAddress, boolean depictAnyChange) {

        Function<AddressDTO, String> countryFunction = _address -> Optional.ofNullable(_address)
                .map(AddressDTO::getCountry)
                .map(this::getCountryLabel)
                .orElse(null);

        addRowSeparator(table, 1);
        checkValuesAndAddRow(table, "Address line 1", address, changedAddress, AddressDTO::getLine1, depictAnyChange);
        checkValuesAndAddRow(table, "Address line 2", address, changedAddress, AddressDTO::getLine2, depictAnyChange);
        checkValuesAndAddRow(table, "Address line 3", address, changedAddress, AddressDTO::getLine3, depictAnyChange);
        checkValuesAndAddRow(table, "Town or city", address, changedAddress, AddressDTO::getCity, depictAnyChange);
        checkValuesAndAddRow(table, "State or province", address, changedAddress, AddressDTO::getStateOrProvince, depictAnyChange);
        checkValuesAndAddRow(table, "Country", address, changedAddress, countryFunction, depictAnyChange);
        checkValuesAndAddRow(table, "Postal code or ZIP", address, changedAddress, AddressDTO::getPostCode, depictAnyChange);
    }

    private void addPhoneNumber(PdfPTable table, PhoneNumberDTO phoneNumber, PhoneNumberDTO changedPhoneNumber, boolean depictAnyChange) {

        String phoneNumber1 = phoneNumber == null ? null : concatValues(phoneNumber.getCountryCode1(), phoneNumber.getPhoneNumber1());
        String changedPhoneNumber1 = changedPhoneNumber == null ? null :  concatValues(changedPhoneNumber.getCountryCode1(), changedPhoneNumber.getPhoneNumber1());
        checkValuesAndAddRow(table, "Phone number 1", phoneNumber1, changedPhoneNumber1, Function.identity(), depictAnyChange);

        String phoneNumber2 = phoneNumber == null ? null : concatValues(phoneNumber.getCountryCode2(), phoneNumber.getPhoneNumber2());
        String changedPhoneNumber2 = changedPhoneNumber == null ? null :  concatValues(changedPhoneNumber.getCountryCode2(), changedPhoneNumber.getPhoneNumber2());
        checkValuesAndAddRow(table, "Phone number 2", phoneNumber2, changedPhoneNumber2, Function.identity(), depictAnyChange);
    }

    private void createOrganisationSection(Document document, AccountDTO accountDTO, boolean isAccountHolderChanged) {
        PdfPTable table = createTable(isAccountHolderChanged);
        createSectionTitle(document, "Organisation details", 14);

        DetailsDTO details = accountDTO.getAccountHolder().getDetails();;
        DetailsDTO changedDetails = null;
        if(isAccountHolderChanged){
            details = Optional.ofNullable(accountDTO.getOldAccountHolder()).map(AccountHolderDTO::getDetails).orElse(null);
            changedDetails = accountDTO.getAccountHolder().getDetails();
        }

        addRowSeparator(table, 1);
        checkValuesAndAddRow(table, "Name", details, changedDetails, DetailsDTO::getName);
        checkValuesAndAddRow(table, "Registration Number", details, changedDetails, DetailsDTO::getRegistrationNumber);

        addRowSeparator(table, 2);

        document.add(table);

        createSectionTitle(document, "Organisation address", 14);

        table = createTable(isAccountHolderChanged);

        AddressDTO address = accountDTO.getAccountHolder().getAddress();
        AddressDTO changedAddress = null;
        if(isAccountHolderChanged){
            address = Optional.ofNullable(accountDTO.getOldAccountHolder()).map(AccountHolderDTO::getAddress).orElse(null);
            changedAddress = accountDTO.getAccountHolder().getAddress();
        }

        addAddressSection(table, address, changedAddress, false);
        addRowSeparator(table, 2);

        document.add(table);
    }

    private void createPublicContactsDetails(Document document, AccountDTO accountDTO, boolean isAccountHolderChanged) {

        AccountHolderRepresentativeDTO primaryContact = accountDTO.getAccountHolderContactInfo().getPrimaryContact();
        AccountHolderRepresentativeDTO changedPrimaryContact = null;
        if(isAccountHolderChanged){
            primaryContact = Optional.ofNullable(accountDTO.getOldAccountHolderContactInfo()).map(AccountHolderContactInfoDTO::getPrimaryContact).orElse(null);
            changedPrimaryContact = accountDTO.getAccountHolderContactInfo().getPrimaryContact();
        }

        createPublicContactDetails(document, primaryContact, changedPrimaryContact, false);

        AccountHolderRepresentativeDTO alternativePrimaryContact = accountDTO.getAccountHolderContactInfo().getAlternativeContact();
        AccountHolderRepresentativeDTO changedAlternativePrimaryContact = null;
        if(isAccountHolderChanged){
            alternativePrimaryContact = Optional.ofNullable(accountDTO.getOldAccountHolderContactInfo())
                    .map(AccountHolderContactInfoDTO::getAlternativeContact).orElse(null);
            changedAlternativePrimaryContact = accountDTO.getAccountHolderContactInfo().getAlternativeContact();
        }

        if (alternativePrimaryContact != null || changedAlternativePrimaryContact != null) {
            createPublicContactDetails(document, alternativePrimaryContact, changedAlternativePrimaryContact, true);
        }
    }
    private void createPublicContactDetails(Document document, AccountHolderRepresentativeDTO primaryContact, AccountHolderRepresentativeDTO changedPrimaryContact, boolean isAlternative) {
        createHorizontalLine(document);

        Long primaryContactId = Optional.ofNullable(primaryContact).map(AccountHolderRepresentativeDTO::getId).orElse(null);
        Long changedPrimaryContactId = Optional.ofNullable(changedPrimaryContact).map(AccountHolderRepresentativeDTO::getId).orElse(null);
        boolean isPrimaryContactChanged = changedPrimaryContactId != null && !Objects.equals(primaryContactId, changedPrimaryContactId);

        String title = isAlternative ? "Alternative Primary Contact details" : "Primary Contact details";
        createSectionTitle(document, title, 14);
        PdfPTable table = createTable(isPrimaryContactChanged);

        LegalRepresentativeDetailsDTO details =
            Optional.ofNullable(primaryContact).map(AccountHolderRepresentativeDTO::getDetails).orElse(null);
        LegalRepresentativeDetailsDTO changedDetails =
            Optional.ofNullable(changedPrimaryContact).map(AccountHolderRepresentativeDTO::getDetails).orElse(null);

        addRowSeparator(table, 1);
        checkValuesAndAddRow(table, "First and middle names", details, changedDetails, LegalRepresentativeDetailsDTO::getFirstName);
        checkValuesAndAddRow(table, "Last name", details, changedDetails, LegalRepresentativeDetailsDTO::getLastName);

        addRow(table, "I confirm that the Primary Contact is aged 18 or over", "Yes", null);
        addRowSeparator(table, 1);
        document.add(table);

        String workTitle = isAlternative ? "Alternative Primary Contact work details" : "Primary Contact work details";
        createSectionTitle(document, workTitle, 14);

        table = createTable(isPrimaryContactChanged);

        addRowSeparator(table, 1);
        checkValuesAndAddRow(table, "Company position", primaryContact, changedPrimaryContact, AccountHolderRepresentativeDTO::getPositionInCompany, isPrimaryContactChanged);

        AddressDTO address = Optional.ofNullable(primaryContact).map(AccountHolderRepresentativeDTO::getAddress).orElse(null);
        AddressDTO changedAddress =
            Optional.ofNullable(changedPrimaryContact).map(AccountHolderRepresentativeDTO::getAddress).orElse(null);
        addAddressSection(table, address, changedAddress, isAlternative);

        PhoneNumberDTO phoneNumber = Optional.ofNullable(primaryContact).map(AccountHolderRepresentativeDTO::getPhoneNumber).orElse(null);
        PhoneNumberDTO changedPhoneNumber =
            Optional.ofNullable(changedPrimaryContact).map(AccountHolderRepresentativeDTO::getPhoneNumber).orElse(null);
        addPhoneNumber(table, phoneNumber, changedPhoneNumber, isAlternative);

        Function<AccountHolderRepresentativeDTO, String>  emailFunction = contact -> Optional.ofNullable(contact)
                .map(AccountHolderRepresentativeDTO::getEmailAddress)
                .map(EmailAddressDTO::getEmailAddress)
                .orElse(null);

        checkValuesAndAddRow(table, "Email", primaryContact, changedPrimaryContact, emailFunction);

        document.add(table);
    }

    private void createAccountDetailsSection(Document document, AccountDTO accountDTO) {

        Optional<SalesContactDetailsDTO> salesContactDetailsDTO = Optional.of(accountDTO)
            .map(AccountDTO::getAccountDetails)
            .map(AccountDetailsDTO::getSalesContactDetails);

        String publicEmail = salesContactDetailsDTO.map(SalesContactDetailsDTO::getEmailAddress).map(EmailAddressDTO::getEmailAddress).orElse("");

        String publicPhone = salesContactDetailsDTO.map(SalesContactDetailsDTO::getPhoneNumberCountryCode).orElse("") +
                salesContactDetailsDTO.map(SalesContactDetailsDTO::getPhoneNumber).orElse("");

        createSectionTitle(document,"2. Account Details", 16);

        createHorizontalLine(document);


        PdfPTable table = createTable(false);
        AccountDetailsDTO accountDetails = accountDTO.getAccountDetails();

        addRowSeparator(table, 1);
        addRow(table, "Account type", getAccountTypeLabel(accountDTO.getAccountType()), null);
        addRow(table, "Account name", accountDetails.getName(), null);
        addRow(table, "Public email address", publicEmail, null);
        addRow(table, "Public phone number", publicPhone, null);

        addRowSeparator(table, 2);
        document.add(table);

        boolean hasBillingDetails = Stream.of(AccountType.TRADING_ACCOUNT, AccountType.PERSON_HOLDING_ACCOUNT)
            .map(Enum::name)
            .anyMatch(s -> s.equals(accountDTO.getAccountType()));

        if (hasBillingDetails) {
            createSectionTitle(document, "Billing details", 14);

            PdfPTable billingTable = createTable(false);
            addAddressSection(billingTable, accountDetails.getAddress(), null, false);

            BillingContactDetailsDTO billingContactDetails = Optional.ofNullable(accountDetails.getBillingContactDetails())
                .orElse(new BillingContactDetailsDTO());
            addRow(billingTable, "SOP Customer ID", billingContactDetails.getSopCustomerId(), null);
            addRow(billingTable, "Contact name", billingContactDetails.getContactName(), null);
            String phone = concatValues(billingContactDetails.getPhoneNumberCountryCode(), billingContactDetails.getPhoneNumber());
            addRow(billingTable, "Phone number", phone, null);
            addRow(billingTable, "Email address", billingContactDetails.getEmail(), null);

            addRowSeparator(billingTable, 2);

            document.add(billingTable);
        }

    }

    private void createTransactionRulesSection(Document document, AccountDTO accountDTO) {

        createSectionTitle(document,"3. Transaction Rules", 16);

        createHorizontalLine(document);


        PdfPTable table = createTable(false);

        addRowSeparator(table, 1);
        if (accountDTO.getTrustedAccountListRules().getRule1() != null) {
            addRow(table, "Do you want a second authorised representative to approve transfers of units to a trusted account?", accountDTO.getTrustedAccountListRules().getRule1() ? "Yes" : "No", null);
        }
        if (accountDTO.getTrustedAccountListRules().getRule2() != null)  {
            addRow(table, "Do you want to allow transfers of units to accounts that are not on the trusted account list?", accountDTO.getTrustedAccountListRules().getRule2() ? "Yes" : "No", null);
        }
        if (accountDTO.getTrustedAccountListRules().getRule3() != null) {
            addRow(table, "Do you want a second authorised representative to approve a surrender transaction or a return of excess allocation?", accountDTO.getTrustedAccountListRules().getRule3() ? "Yes" : "No", null);
        }

        addRowSeparator(table, 2);

        document.add(table);
    }

    private void createOperatorSection(Document document, AccountDTO accountDTO) {

        if (accountDTO.getOperator() == null) {
            return;
        }

        boolean isInstallation = "INSTALLATION".equals(accountDTO.getOperator().getType());
        boolean isAircraftOperator = "AIRCRAFT_OPERATOR".equals(accountDTO.getOperator().getType());
        boolean isMaritimeOperator = "MARITIME_OPERATOR".equals(accountDTO.getOperator().getType());
        boolean isInstallationTransfer = "INSTALLATION_TRANSFER".equals(accountDTO.getOperator().getType());

        if (isInstallation) {
            createSectionTitle(document,"4. Installation", 16);
        } else if (isAircraftOperator) {
            createSectionTitle(document,"4. Aircraft Operator", 16);
        }
        else if (isMaritimeOperator) {
            createSectionTitle(document,"4. Maritime Operator", 16);
        }
        else if (isInstallationTransfer) {
            createSectionTitle(document,"4. Installation Transfer", 16);
        }

        createHorizontalLine(document);

        Optional<RegulatorType> changedRegulator =
            Optional.of(accountDTO.getOperator()).map(OperatorDTO::getChangedRegulator);
        boolean isRegulatorChanged = changedRegulator.isPresent() && changedRegulator.get() != accountDTO.getOperator().getRegulator();

        if (isRegulatorChanged) {
            createChangedLabelRow(document);
            createHorizontalLine(document);
        }

        PdfPTable table = createTable(isRegulatorChanged);

        addRowSeparator(table, 1);

        if (isInstallation) {

            addRow(table, "Installation name", accountDTO.getOperator().getName(), null);
            addRow(table, "Installation activity type", getActivityTypes(accountDTO.getOperator()), null);
            addRow(table, "Permit ID", accountDTO.getOperator().getPermit().getId(), null);
            addRow(table, "Regulator", accountDTO.getOperator().getRegulator().name(),
                    isRegulatorChanged
                            ? accountDTO.getOperator().getChangedRegulator().name()
                            : null);
            addRow(table, "First year of verified emission submission", accountDTO.getOperator().getFirstYear() != null ? Integer.toString(accountDTO.getOperator().getFirstYear()) : "", null);
            addRow(table, "Last year of verified emission submission", accountDTO.getOperator().getLastYear() != null ? Integer.toString(accountDTO.getOperator().getLastYear()) : "", null);
        } else if (isAircraftOperator) {
            addRow(table, "Monitoring plan ID", accountDTO.getOperator().getMonitoringPlan().getId(), null);
            addRow(table, "Regulator", accountDTO.getOperator().getRegulator().name(), isRegulatorChanged ? accountDTO.getOperator().getChangedRegulator().name() : null);
            addRow(table, "First year of verified emission submission", accountDTO.getOperator().getFirstYear() != null ? Integer.toString(accountDTO.getOperator().getFirstYear()) : "", null);
            addRow(table, "Last year of verified emission submission", accountDTO.getOperator().getLastYear() != null ? Integer.toString(accountDTO.getOperator().getLastYear()) : "", null);
        } else if (isMaritimeOperator) {
            addRow(table, "Monitoring plan ID", accountDTO.getOperator().getMonitoringPlan().getId(), null);
            addRow(table, "Regulator", accountDTO.getOperator().getRegulator().name(), isRegulatorChanged ? accountDTO.getOperator().getChangedRegulator().name() : null);
            addRow(table, "First year of verified emission submission", accountDTO.getOperator().getFirstYear() != null ? Integer.toString(accountDTO.getOperator().getFirstYear()) : "", null);
            addRow(table, "Last year of verified emission submission", accountDTO.getOperator().getLastYear() != null ? Integer.toString(accountDTO.getOperator().getLastYear()) : "", null);
            addRow(table, "Company IMO number", Optional.ofNullable(accountDTO.getOperator().getImo()).orElse(""), null);
        } else if (isInstallationTransfer) {
            createSectionTitle(document, "Installation to be transferred", 14);
            table = createTable(isRegulatorChanged);

            addRowSeparator(table, 1);
            addRow(table, "Installation ID", Long.toString(accountDTO.getOperator().getIdentifier()), null);
            addRow(table, "Installation name", accountDTO.getOperator().getName(), null);
            addRow(table, "Installation activity type", getActivityTypes(accountDTO.getInstallationToBeTransferred()), null);
            addRow(table, "Permit ID", accountDTO.getInstallationToBeTransferred().getPermit().getId(), null);
            addRow(table, "Regulator", accountDTO.getInstallationToBeTransferred().getRegulator().name(),
                    isRegulatorChanged
                            ? accountDTO.getInstallationToBeTransferred().getChangedRegulator().name()
                            : null);
            addRow(table, "First year of verified emission submission", accountDTO.getInstallationToBeTransferred().getFirstYear() != null ? Integer.toString(accountDTO.getInstallationToBeTransferred().getFirstYear()) : "", null);
            addRow(table, "Last year of verified emission submission", accountDTO.getInstallationToBeTransferred().getLastYear() != null ? Integer.toString(accountDTO.getInstallationToBeTransferred().getLastYear()) : "", null);
            addRowSeparator(table, 1);

            document.add(table);
            createSectionTitle(document, "Installation details", 14);

            table = createTable(isRegulatorChanged);
            addRowSeparator(table, 1);
            addRow(table, "New installation name", accountDTO.getInstallationToBeTransferred().getName(), null);
            addRow(table, "New permit ID", accountDTO.getInstallationToBeTransferred().getPermit().getId(), null);

        }
        addRowSeparator(table, 2);

        document.add(table);
    }

    private String getActivityTypes(OperatorDTO operatorDTO) {
        if (CollectionUtils.isEmpty(operatorDTO.getActivityTypes())) {
            return operatorDTO.getActivityType().name();
        }
        return operatorDTO.getActivityTypes().stream()
                    .map(Enum::name)
                    .map(this::getActivityTypeLabel)
                    .collect(Collectors.joining(";"));
    }

    public void createAuthorisedRepresentativesSection(Document document, AccountDTO accountDTO) {

        createSectionTitle(document,"5. Authorised Representatives", 16);

        createHorizontalLine(document);

        if (accountDTO.getAuthorisedRepresentatives().isEmpty()) {
            String message = "No Authorised Representatives were added.";
            PdfPTable table = new PdfPTable(3);
            table.setTotalWidth(DocumentHelper.TABLE_WIDTH);
            table.setWidths(new int[]{0, DocumentHelper.TABLE_WIDTH, 0});
            table.setWidthPercentage(DocumentHelper.TABLE_WIDTH_PERCENTAGE);
            addRow(table, "", message, null);
            document.add(table);
        }

        for (int i=0; i<accountDTO.getAuthorisedRepresentatives().size(); i++) {
            AuthorisedRepresentativeDTO authRep = accountDTO.getAuthorisedRepresentatives().get(i);
            createSectionTitle(document,"Authorised Representative "+(i+1), 14);

            PdfPTable table = createTable(false);

            addRowSeparator(table, 1);
            addRow(table, "Name", authRep.getFirstName() + " " + authRep.getLastName(), null);
            addRow(table, "User ID", authRep.getUrid(), null);
            addRow(table, "Permissions", getAccountRightLabel(authRep.getRight().name()), null);

            addRowSeparator(table, 2);

            document.add(table);
        }

    }

    private void createSectionTitle(Document document, String text, int fontSize) {
        PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(DocumentHelper.TABLE_WIDTH);
        table.setWidths(new int[]{DocumentHelper.TABLE_WIDTH});
        table.setWidthPercentage(DocumentHelper.TABLE_WIDTH_PERCENTAGE);

        Phrase phrase = new Phrase(
            text, FontFactory.getFont(new Font(helper.getAppFont()).getFamilyname(), fontSize, Font.BOLD));

        PdfPCell cell = new PdfPCell(phrase);
        cell.setPaddingLeft(0);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        document.add(table);
    }

    private void createGenericInfoSection(Document document, Task task) {
        PdfPTable table = createTable(false);

        addRowSeparator(table, 2);
        addRow(table, "Request ID", task.getRequestId().toString(), null);
        addRow(table, "Created on (UTC)", task.getInitiatedDate().toString(), null);
        String initiatorName = Optional.ofNullable(task.getInitiatedBy().getKnownAs())
                .orElse(task.getInitiatedBy().getFirstName() + " " + task.getInitiatedBy().getLastName());

        boolean isInitiatorAdmin = this.userService.hasAnyAdminRole(task.getInitiatedBy().getIamIdentifier());

        if (isInitiatorAdmin) {
            initiatorName = "Registry Administrator";
        }
        addRow(table, "Initiator", initiatorName, null);
        addRowSeparator(table, 3);

        document.add(table);
    }

    private void createHorizontalLine(Document document) {
        PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(DocumentHelper.CONTENT_WIDTH);
        table.setWidthPercentage(DocumentHelper.TABLE_WIDTH_PERCENTAGE);

        PdfPCell emptyCell = new PdfPCell(new Paragraph(""));
        emptyCell.setFixedHeight(5);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyCell);

        PdfPCell cell = new PdfPCell(new Paragraph(""));
        cell.setFixedHeight(1);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(DocumentHelper.APP_LIGHT_GREY);
        table.addCell(cell);

        emptyCell = new PdfPCell(new Paragraph(""));
        emptyCell.setFixedHeight(5);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyCell);

        document.add(table);
    }

    private void addRowSeparator(PdfPTable table, int lines) {
        for (int i=0; i<lines; i++) {
            addRow(table, "", "", null);
        }
    }

    private <T> void checkValuesAndAddRow(PdfPTable table, String label, T initial, T changed, Function<T, String> mapper) {

        String initialValue = Optional.ofNullable(initial).map(mapper).orElse(null);
        if (hasChange(initial, changed, mapper)) {
            String changedValue = mapper.apply(changed);
            addRow(table, label, initialValue, changedValue);
        } else {
            addRow(table, label, initialValue, null);
        }
    }

    private <T> boolean hasChange(T initial, T changed, Function<T, String> mapper) {

        if (changed == null) {
            return false;
        }

        if (initial == null) {
            return true;
        }

        String changedValue = mapper.apply(changed);
        String originalValue = mapper.apply(initial);

        return !equalsOrEmpty(originalValue, changedValue);
    }

    private <T> void checkValuesAndAddRow(PdfPTable table, String label, T initial, T changed, Function<T, String> mapper, boolean depictAnyChange) {

        if (depictAnyChange) {
            String initialValue = Optional.ofNullable(initial).map(mapper).orElse("");
            String changedValue = Optional.ofNullable(changed).map(mapper).orElse("");
            if (equalsOrEmpty(initialValue, changedValue)) {
                addRow(table, label, initialValue, null);
            } else {
                addRow(table, label, initialValue, changedValue);
            }
        } else {
            checkValuesAndAddRow(table, label, initial, changed, mapper);
        }
    }

    private boolean equalsOrEmpty(String first, String second) {
        String v1 = Optional.ofNullable(first).filter(s -> !s.isBlank()).orElse(null);
        String v2 = Optional.ofNullable(second).filter(s -> !s.isBlank()).orElse(null);
        return Objects.equals(v1, v2);
    }

    private void addRow(PdfPTable table, String label, String value, String changedValue) {
        Font labelFont = new Font(helper.getAppFont().getFamily(), 10, Font.BOLD);
        Font valueFont = new Font(helper.getAppFont().getFamily(), 10, Font.BOLD, DocumentHelper.APP_GREY);
        int border = Rectangle.NO_BORDER;
        int verticalAlignment = Element.ALIGN_TOP;
        int paddingLeft = 0;
        int paddingRight = 10;
        int padding = 4;

        PdfPCell cell = new PdfPCell(new Paragraph(label, labelFont));
        cell.setVerticalAlignment(verticalAlignment);
        cell.setPadding(padding);
        cell.setPaddingRight(paddingRight);
        cell.setPaddingLeft(paddingLeft);
        cell.setBorder(border);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(value, valueFont));
        cell.setPadding(padding);
        cell.setPaddingRight(paddingRight);
        cell.setPaddingLeft(paddingLeft);
        cell.setVerticalAlignment(verticalAlignment);
        cell.setBorder(border);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(changedValue != null ? changedValue : "", valueFont));
        cell.setPadding(padding);
        cell.setPaddingLeft(paddingLeft);
        cell.setVerticalAlignment(verticalAlignment);

        if (changedValue != null) {
            int changedBorderWidth = 5;

            cell.setBackgroundColor(DocumentHelper.APP_YELLOW_LIGHT);
            cell.setBorderColorLeft(DocumentHelper.APP_YELLOW_DARK);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderColorTop(Color.WHITE);
            cell.setBorderWidthLeft(changedBorderWidth);
            cell.setPaddingLeft(paddingLeft + changedBorderWidth + 2F);
            cell.setBorderColorLeft(Color.ORANGE);
        } else {
            cell.setBorder(border);
        }
        table.addCell(cell);

    }

    private void createChangedLabelRow(Document document) {
        PdfPTable table = createTable(true);

        Font labelFont = new Font(helper.getAppFont().getFamily(), 10, Font.BOLD);
        int border = Rectangle.NO_BORDER;
        int verticalAlignment = Element.ALIGN_TOP;
        int horizontalAlignment = Element.ALIGN_LEFT;
        int paddingLeft = 0;
        int paddingRight = 10;
        int padding = 4;

        addRowSeparator(table, 1);
        PdfPCell cell = new PdfPCell(new Paragraph("Field", labelFont));
        cell.setVerticalAlignment(verticalAlignment);
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setPadding(padding);
        cell.setPaddingRight(paddingRight);
        cell.setPaddingLeft(paddingLeft);
        cell.setBorder(border);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Original value", labelFont));
        cell.setPadding(padding);
        cell.setPaddingRight(paddingRight);
        cell.setPaddingLeft(paddingLeft);
        cell.setVerticalAlignment(verticalAlignment);
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setBorder(border);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Changed value", labelFont));
        cell.setPadding(padding);
        cell.setPaddingRight(paddingRight);
        cell.setPaddingLeft(paddingLeft);
        cell.setVerticalAlignment(verticalAlignment);
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setBorder(border);
        table.addCell(cell);

        document.add(table);
    }

    private String getActivityTypeLabel(String activityType) {
        return helper.getActivityType(activityType);
    }

    private String getAccountRightLabel(String right) {
        Map<String, String> rights = new HashMap<>();
        rights.put("ROLE_BASED", "Role based");
        rights.put("INITIATE_AND_APPROVE", "Initiate and approve transactions and Trusted Account List (TAL) updates");
        rights.put("APPROVE", "Approve transfers and Trusted Account List (TAL) updates");
        rights.put("INITIATE", "Initiate transfers and Trusted Account List (TAL) updates");
        rights.put("SURRENDER_INITIATE_AND_APPROVE", "Initiate and approve Surrender of allowances and Return of excess allocation transactions");
        rights.put("READ_ONLY", "Read only");
        return rights.get(right);
    }

    private String getCountryLabel(String countryCode) {
        return helper.getCountry(countryCode);
    }

    private String getAccountTypeLabel(String accountType) {
        Map<String, String> types = new HashMap<>();
        types.put("OPERATOR_HOLDING_ACCOUNT", "ETS - Operator Holding Account");
        types.put("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT", "ETS - Aircraft Operator Holding Account");
        types.put("TRADING_ACCOUNT", "ETS - Trading Account");
        types.put("PERSON_HOLDING_ACCOUNT", "KP - Person Holding Account");
        return types.get(accountType);
    }

    private PdfPTable createTable(boolean hasThreeColumns) {
        PdfPTable table = new PdfPTable(3);
        table.setTotalWidth(DocumentHelper.TABLE_WIDTH);
        if (hasThreeColumns) {
            table.setWidths(DocumentHelper.THREE_COLUMN_WIDTH);
        } else {
            table.setWidths(DocumentHelper.TWO_COLUMN_WIDTH);
        }
        table.setWidthPercentage(DocumentHelper.TABLE_WIDTH_PERCENTAGE);
        return table;
    }

    private String concatValues(String... values) {
        return Stream.of(values).filter(Objects::nonNull).collect(Collectors.joining());
    }
}