package gov.uk.ets.registry.api.task.printenrolmentletter;

import com.lowagie.text.*;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.pdf.PdfWriter;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.printenrolmentletter.service.PrintEnrolmentLetterConfig;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for printing enrollment key task.
 */
@Log4j2
@Service
@AllArgsConstructor
public class PrintEnrolmentLetterTaskService implements TaskTypeService<TaskDetailsDTO> {

    private final PersistenceService persistenceService;
    private final UserRepository userRepository;
    private final PrintEnrolmentLetterConfig config;
    private final TaskRepository taskRepository;
    private final TaskEventService taskEventService;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.PRINT_ENROLMENT_LETTER_REQUEST);
    }

    @Override
    public TaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        return taskDetailsDTO;
    }

    @Protected( {
            OnlyRegistryAdminCanCompleteTaskRule.class
    })
    @Override
    @Transactional
    public TaskCompleteResponse complete(TaskDetailsDTO taskDTO, TaskOutcome taskOutcome, String comment) {
        return defaultResponseBuilder(taskDTO).build();
    }

    @Override
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        Task task = taskRepository.findByRequestId(infoDTO.getTaskRequestId());
        UploadedFile file = new UploadedFile();
        file.setFileData(task.getFile());
        file.setFileName("registry_activation_code_" + task.getInitiatedDate().getTime() + ".pdf");
        return file;
    }

    @Protected( {
            OnlySeniorOrJuniorCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {

    }

    @Protected( {
            OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule.class,
    })
    @Override
    public void checkForInvalidAssignPermissions() {

    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public Task create(User user, User initiator,Task parent) {
        Task task = new Task();
        Date initiationDate = new Date();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        task.setType(RequestType.PRINT_ENROLMENT_LETTER_REQUEST);
        task.setInitiatedBy(initiator);
        task.setInitiatedDate(initiationDate);
        task.setUser(user);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setFile(generate(user.getId(), task.getInitiatedDate()));
        task.setParentTask(parent);

        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, initiator.getUrid());

        return task;
    }

    public byte[] generate(Long userId, Date creationDate) {
        User user = userRepository.getOne(userId);

        Document document = new Document(PageSize.A4, 70, 70, 70, 70);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, stream);
            document.open();
            document.addAuthor("Environment Agency");
            document.addTitle("Registry activation code");

            // LOGO
            ResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:" + config.getLogoPath());
            Image logo = Image.getInstance(resource.getURI().toURL());
            logo.scalePercent(50);
            logo.setAlignment(Image.RIGHT);
            document.add(logo);

            // HEADER
            document.add(new Paragraph());
            Table table = new Table(2);
            table.setWidth(100);
            table.setHorizontalAlignment(HorizontalAlignment.LEFT);
            table.setBorder(Rectangle.NO_BORDER);
            Font font = new Font(Font.HELVETICA, 11);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Cell cell = new Cell(new Phrase(config.getHeaderPrivate(), font));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            cell = new Cell(new Phrase(config.getHeaderDate() + simpleDateFormat.format(creationDate), font));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            document.add(table);

            // SALUTATION
            Paragraph salutationParagraph = new Paragraph();
            salutationParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            salutationParagraph.setSpacingBefore(30);
            salutationParagraph.setSpacingAfter(10);
            salutationParagraph.add(new Chunk(config.getSalutation(), font));
            salutationParagraph.add(new Chunk(user.getFirstName() + " " + user.getLastName() + ",", font));
            document.add(salutationParagraph);

            // ACTIVATION CODE
            Paragraph activationCodeIntroParagraph = new Paragraph();
            activationCodeIntroParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            activationCodeIntroParagraph.setSpacingAfter(10);
            activationCodeIntroParagraph.add(new Chunk(config.getParagraph1Title() + "\n\n", boldFont));
            activationCodeIntroParagraph.add(new Chunk(config.getParagraph1(), font));
            document.add(activationCodeIntroParagraph);

            Paragraph activationCodeParagraph = new Paragraph();
            activationCodeParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
            activationCodeParagraph.setSpacingAfter(10);
            activationCodeParagraph.add(new Chunk(user.getEnrolmentKey().replace("-", " "), boldFont));
            activationCodeParagraph.add(new Chunk("\n\n " + config.getParagraph1MoreInfo1(), font));
            activationCodeParagraph.add(new Chunk("\n\n " + config.getParagraph1MoreInfo2(), font));
            document.add(activationCodeParagraph);

            // ENROLMENT STEPS
            Paragraph introToSteps = new Paragraph();
            introToSteps.setAlignment(Element.ALIGN_JUSTIFIED);
            introToSteps.setSpacingBefore(10);
            introToSteps.setSpacingAfter(10);
            introToSteps.add(new Chunk(config.getParagraph2Title() + "\n\n", boldFont));
            introToSteps.add(new Chunk(config.getParagraph2(), font));
            document.add(introToSteps);

            List list = new List(List.UNORDERED);
            list.setListSymbol("•    ");
            list.setIndentationLeft(15);
            list.add(new ListItem(config.getListItem1() + " " + config.getApplicationUrl(), font));
            list.add(new ListItem(config.getListItem2(), font));
            list.add(new ListItem(config.getListItem3(), font));
            list.add(new ListItem(config.getListItem4(), font));
            list.add(new ListItem(config.getListItem5(), font));
            document.add(list);

            Paragraph paragraph2MoreInfo = new Paragraph();
            paragraph2MoreInfo.setAlignment(Element.ALIGN_JUSTIFIED);
            paragraph2MoreInfo.setSpacingBefore(10);
            paragraph2MoreInfo.setSpacingAfter(10);
            paragraph2MoreInfo.add(new Chunk(config.getParagraph2MoreInfo(), font));
            document.add(paragraph2MoreInfo);

            // PARAGRAPH 3
            Paragraph paragraph3 = new Paragraph();
            paragraph3.setAlignment(Element.ALIGN_JUSTIFIED);
            paragraph3.setSpacingBefore(10);
            paragraph3.setSpacingAfter(10);
            paragraph3.add(new Chunk(config.getParagraph3Title() + "\n\n", boldFont));
            paragraph3.add(new Chunk(config.getParagraph3() + "\n", font));
            document.add(paragraph3);

            List listEmail = new List(List.UNORDERED);
            listEmail.setListSymbol("• ");
            listEmail.add(new ListItem(config.getParagraph3MoreInfo(), font));
            document.add(listEmail);

            // PARAGRAPH 4
            Paragraph paragraph4 = new Paragraph();
            paragraph4.setAlignment(Element.ALIGN_JUSTIFIED);
            paragraph4.setSpacingBefore(20);
            paragraph4.setSpacingAfter(10);
            paragraph4.add(new Chunk(config.getParagraph4Title() + "\n\n", font));
            paragraph4.add(new Chunk(config.getParagraph4(), boldFont));
            document.add(paragraph4);

        } catch (DocumentException | IOException ex) {
            log.error("There was an error in generating the PDF document");
        }
        document.close();

        return stream.toByteArray();
    }
}
