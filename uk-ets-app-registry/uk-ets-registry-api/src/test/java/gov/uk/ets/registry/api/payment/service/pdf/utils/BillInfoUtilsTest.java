package gov.uk.ets.registry.api.payment.service.pdf.utils;

import com.lowagie.text.Paragraph;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.BillingContactDetailsDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.payment.service.pdf.PdfFormatter;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BillInfoUtilsTest {

    private PdfFormatter formatter;
    private TaskRepository taskRepository;
    private AccountRepository accountRepository;
    private Mapper mapper;
    private AccountDTOFactory accountDTOFactory;

    private BillInfoUtils billInfoUtils;

    @BeforeEach
    void setUp() {
        formatter = mock(PdfFormatter.class);
        taskRepository = mock(TaskRepository.class);
        accountRepository = mock(AccountRepository.class);
        mapper = mock(Mapper.class);
        accountDTOFactory = mock(AccountDTOFactory.class);

        billInfoUtils = new BillInfoUtils(formatter, taskRepository, accountRepository, accountDTOFactory, mapper);
    }

    @Test
    void testGetBillingParagraph_withAccountFound() {
        Task parentTask = new Task();
        parentTask.setType(RequestType.ACCOUNT_OPENING_REQUEST);

        // Setup
        Long taskId = 123L;
        Task task = new Task();
        task.setId(taskId);
        task.setType(RequestType.PAYMENT_REQUEST);
        task.setParentTask(parentTask);
        when(taskRepository.findByRequestId(taskId)).thenReturn(task);

        Account parentAccount = new Account();
        parentAccount.setId(123L);
        parentAccount.setAccountName("Contact X");


        parentAccount.setAccountHolder(dummyAccountHolder());
        parentTask.setAccount(parentAccount);
        when(accountRepository.findById(taskId)).thenReturn(Optional.of(parentAccount));

        when(formatter.labelWithBoldValue(eq("Contact Name: "), eq("Contact X"))).thenReturn(new Paragraph("Contact Name"));
        when(formatter.labelWithBoldValue(eq("Billed To: "), anyString())).thenReturn(new Paragraph("Billed To"));
        when(formatter.labelWithBoldValue(eq("Address: "), anyString())).thenReturn(new Paragraph("Address"));

        Paragraph result = billInfoUtils.getBillingParagraph(taskId, true);
        assertNotNull(result);
        assertEquals(8, result.getChunks().size() + result.size()); // Paragraphs may contain sub-elements or chunks

        verify(formatter).labelWithBoldValue(eq("Contact Name: "), eq("AH Contact X"));
        verify(formatter, atLeastOnce()).labelWithBoldValue(eq("Address: "), anyString());
    }

    @Test
    void testGetBillingParagraph_withFallbackToAccountDTO() {
        Long taskId = 456L;
        Task task = new Task();
        task.setId(taskId);
        Account account = new Account();
        account.setId(8548L);
        account.setAccountHolder(dummyAccountHolder());
        task.setAccount(account);
        task.setType(RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST);
        when(taskRepository.findByRequestId(taskId)).thenReturn(task);

        AccountDTO accountDTO = mock(AccountDTO.class);
        AccountHolderDTO holderDTO = mock(AccountHolderDTO.class);
        BillingContactDetailsDTO billingDTO = mock(BillingContactDetailsDTO.class);
        AccountDetailsDTO detailsDTO = mock(AccountDetailsDTO.class);

        when(accountDTOFactory.create(task.getAccount())).thenReturn(accountDTO);
        when(accountDTO.getAccountHolder()).thenReturn(holderDTO);
        when(accountDTO.getAccountDetails()).thenReturn(detailsDTO);
        when(detailsDTO.getBillingContactDetails()).thenReturn(billingDTO);

        when(billingDTO.getContactName()).thenReturn("Billing Contact");

        when(holderDTO.actualName()).thenReturn("Holder Co.");
        when(formatter.labelWithBoldValue(eq("Contact Name: "), eq("Billing Contact"))).thenReturn(new Paragraph("Contact Name"));
        when(formatter.labelWithBoldValue(eq("Billed To: "), eq("Holder Co."))).thenReturn(new Paragraph("Billed To"));
        when(formatter.labelWithBoldValue(eq("Address: "), anyString())).thenReturn(new Paragraph("Address"));

        Paragraph result = billInfoUtils.getBillingParagraph(taskId, true);
        assertNotNull(result);
        verify(formatter).labelWithBoldValue("Contact Name: ", "AH Contact X");
    }

    @Test
    void testGetBillingParagraph_fromAccountDirectly() {
        Account account = mock(Account.class);
        AccountHolder holder = mock(AccountHolder.class);
        Contact contact = mock(Contact.class);

        when(account.getAccountHolder()).thenReturn(holder);
        when(holder.getName()).thenReturn("Direct Name");
        when(holder.getContact()).thenReturn(contact);

        when(contact.getLine1()).thenReturn("L1");
        when(contact.getCity()).thenReturn("City");
        when(contact.getCountry()).thenReturn("Country");

        when(formatter.labelWithBoldValue(eq("Contact Name: "), eq("Direct Name"))).thenReturn(new Paragraph("Contact Name"));
        when(formatter.labelWithBoldValue(eq("Billed To: "), anyString())).thenReturn(new Paragraph("Billed To"));
        when(formatter.labelWithBoldValue(eq("Address: "), anyString())).thenReturn(new Paragraph("Address"));

        Paragraph result = billInfoUtils.getBillingParagraph(account);

        assertNotNull(result);
        verify(formatter).labelWithBoldValue(eq("Contact Name: "), eq("Direct Name"));
    }

    private AccountHolder dummyAccountHolder() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setId(123L);
        accountHolder.setName("AH Contact X");
        Contact ahConact = new Contact();
        ahConact.setId(123L);
        ahConact.setLine1("Line 1");
        ahConact.setLine2("Line 2");
        ahConact.setLine3("Line 3");
        ahConact.setPostCode("12345");
        ahConact.setPhoneNumber1("221555666");
        accountHolder.setContact(ahConact);
        return accountHolder;
    }
}
