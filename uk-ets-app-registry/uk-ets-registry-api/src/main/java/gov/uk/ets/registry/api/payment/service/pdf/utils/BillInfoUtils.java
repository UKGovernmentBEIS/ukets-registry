package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.Paragraph;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.BillingContactDetailsDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillInfoUtils {
    private static final String CUSTOMER_NAME = "Customer Name: ";
    private static final String BILLED_TO = "Billed To: ";
    private static final String ADDRESS = "Address: ";
    private final PdfFormatter formater;

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final AccountDTOFactory accountDTOFactory;
    private final Mapper mapper;

    public Paragraph getBillingParagraph(Long parentTaskRequestId, boolean isInvoice) {
        Objects.requireNonNull(parentTaskRequestId, "Parent task id cannot be null");
        Task task = taskRepository.findByRequestId(parentTaskRequestId);
        Account account = retrieveAccount(task);
        if (account != null) {
           return getBillingParagraph(account);
        }
        return getBillingParagraph(retrieveAccountDTO(task), isInvoice);
    }

    private Paragraph getBillingParagraph(AccountDTO accountDTO, boolean isInvoice) {
        AccountHolderDTO accountHolder = accountDTO.getAccountHolder();
        BillingContactDetailsDTO billingContactDetails = accountDTO.getAccountDetails().getBillingContactDetails();

        Paragraph boxContent = new Paragraph();
        if (isInvoice) {
            boxContent.add(
                    formater.labelWithBoldValue(CUSTOMER_NAME, billingContactDetails != null && billingContactDetails.getContactName() != null ?
                            billingContactDetails.getContactName()
                            : getCustomerName(accountHolder)));
            boxContent.add(
                    formater.labelWithBoldValue(BILLED_TO, accountHolder.actualName()));
        } else {
            boxContent.add(
                    formater.boldParagraph(billingContactDetails != null && billingContactDetails.getContactName() != null ?
                            billingContactDetails.getContactName()
                            : getCustomerName(accountHolder)));
        }
        boxContent.add(
                formater.labelWithBoldValue(ADDRESS,
                        billingContactDetails != null && billingContactDetails.getContactName() != null ?
                                billingContactDetails.getContactName()
                                : getAddress(accountHolder.getAddress())));
        return boxContent;
    }


    public Paragraph getBillingParagraph(Account account) {
        AccountHolder accountHolder = account.getAccountHolder();
        Paragraph boxContent = new Paragraph();
        boxContent.add(
                formater.labelWithBoldValue(CUSTOMER_NAME, accountHolder.getName()));
        boxContent.add(
                formater.labelWithBoldValue(BILLED_TO, getAddress(accountHolder.getContact())));
        boxContent.add(
                formater.labelWithBoldValue(ADDRESS, getAddress(accountHolder.getContact())));
        return boxContent;
    }

    private String getAddress(Contact contact) {
        return Stream.of(
                        contact.getLine1(),
                        contact.getLine2(),
                        contact.getLine3(),
                        contact.getCity(),
                        contact.getPostCode(),
                        contact.getCountry()
                )
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));

    }

    private String getAddress(AddressDTO address) {
        return Stream.of(
                        address.getLine1(),
                        address.getLine2(),
                        address.getLine3(),
                        address.getCity(),
                        address.getPostCode(),
                        address.getCountry()
                )
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));

    }

    private String getCustomerName(AccountHolderDTO accountHolder) {
        return accountHolder.getDetails().getName();
    }

    private Account retrieveAccount(Task task) {
        return accountRepository.findById(task.getId()).orElse(null);
    }

    private AccountDTO retrieveAccountDTO(Task task) {
        if (task.getType().equals(RequestType.ACCOUNT_OPENING_REQUEST)) {
            return mapper.convertToPojo(task.getDifference(), AccountDTO.class);
        }
        return accountDTOFactory.create(task.getAccount());
    }
}
