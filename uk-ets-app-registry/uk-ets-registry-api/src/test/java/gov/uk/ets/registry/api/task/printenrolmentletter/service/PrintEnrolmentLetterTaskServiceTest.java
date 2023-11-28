package gov.uk.ets.registry.api.task.printenrolmentletter.service;

import static org.junit.Assert.assertTrue;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.task.printenrolmentletter.PrintEnrolmentLetterTaskService;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PrintEnrolmentLetterConfig.class})
public class PrintEnrolmentLetterTaskServiceTest {

    @Mock
    private PersistenceService persistenceService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventService taskEventService;

    @Autowired
    public PrintEnrolmentLetterConfig config;

    private PrintEnrolmentLetterTaskService printEnrolmentLetterTaskService;

    @Value("${application.url}")
    String applicationUrl;

    private User taskUser;
    private Date date = new Date();

    @Before
    public void setup() {
        taskUser = new User();
        taskUser.setId(12345L);
        taskUser.setUrid("UK1234567890");
        taskUser.setFirstName("Test");
        taskUser.setLastName("User");
        taskUser.setEnrolmentKey("12345678901234567890");
        MockitoAnnotations.initMocks(this);
        Mockito.when(userRepository.getOne(12345L)).thenReturn(taskUser);
        printEnrolmentLetterTaskService =
            new PrintEnrolmentLetterTaskService(persistenceService, userRepository, config, taskRepository,
                taskEventService);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Test
    public void generatePdf() throws IOException {
        PdfReader reader = new PdfReader(printEnrolmentLetterTaskService.generate(taskUser.getId(), date));
        PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
        String contentOfPDF = pdfTextExtractor.getTextFromPage(1);

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        assertTrue(contentOfPDF.contains(simpleDateFormat.format(date)));

        assertTrue(contentOfPDF.contains(taskUser.getFirstName() + " " + taskUser.getLastName()));
        assertTrue(contentOfPDF.contains(taskUser.getEnrolmentKey()));

        assertTrue(contentOfPDF.contains(applicationUrl));
    }
}
