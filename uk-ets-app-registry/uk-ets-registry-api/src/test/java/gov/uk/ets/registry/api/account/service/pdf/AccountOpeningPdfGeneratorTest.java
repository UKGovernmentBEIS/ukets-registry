package gov.uk.ets.registry.api.account.service.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.IOException;
import java.sql.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
class AccountOpeningPdfGeneratorTest {

    private final Mapper mapper = new Mapper(new ObjectMapper());
    private final DocumentHelper helper = new DocumentHelper(mapper);
    private final HeaderAndFooterPageEventHelper headerAndFooterPageEventHelper = new HeaderAndFooterPageEventHelper(helper);
    @MockBean
    private UserService userService;

    private AccountOpeningPdfGenerator accountOpeningPdfGenerator;

    @BeforeEach
    public void setup() {
        accountOpeningPdfGenerator = new AccountOpeningPdfGenerator(userService, mapper, helper, headerAndFooterPageEventHelper);
        ReflectionTestUtils.setField(helper, "countriesResource", new ClassPathResource("data/countries.json"));
        ReflectionTestUtils.setField(helper, "activityTypesResource", new ClassPathResource("data/activityTypes.json"));
        helper.init();
    }

    @Test
    void test() throws IOException {
        // given
        Task task = createTask();
        Mockito.when(userService.hasAnyAdminRole("iamIdentifier")).thenReturn(true);

        byte[] expected = new ClassPathResource("pdf/expected.pdf").getInputStream().readAllBytes();
        PdfReader expectedReader = new PdfReader(expected);
        PdfTextExtractor expectedExtractor = new PdfTextExtractor(expectedReader);

        // when
        byte[] pdf = accountOpeningPdfGenerator.generate(task);

        PdfReader pdfReader = new PdfReader(pdf);
        PdfTextExtractor pdfExtractor = new PdfTextExtractor(pdfReader);

        // then
        Assertions.assertEquals(expectedReader.getNumberOfPages(), pdfReader.getNumberOfPages());
        Assertions.assertEquals(3, pdfReader.getNumberOfPages());
        Assertions.assertEquals(expectedExtractor.getTextFromPage(1), pdfExtractor.getTextFromPage(1));
        Assertions.assertEquals(expectedExtractor.getTextFromPage(2), pdfExtractor.getTextFromPage(2));
    }

    private Task createTask() {
        String difference = "{\n" +
            "  \"accountType\": \"TRADING_ACCOUNT\",\n" +
            "  \"accountHolder\": {\n" +
            "    \"id\": 1000018,\n" +
            "    \"emailAddress\": {\n" +
            "      \"emailAddress\": \"test_dp@mailinator.com\",\n" +
            "      \"emailAddressConfirmation\": \"test_dp@mailinator.com\"\n" +
            "    },\n" +
            "    \"address\": {\n" +
            "      \"country\": \"UK\",\n" +
            "      \"postCode\": \"SW1V 4LA\",\n" +
            "      \"townOrCity\": \"Unknown\",\n" +
            "      \"buildingAndStreet\": \"Chadwick Street\",\n" +
            "      \"buildingAndStreet2\": \"\",\n" +
            "      \"buildingAndStreet3\": \"\"\n" +
            "    },\n" +
            "    \"phoneNumber\": {\n" +
            "      \"phoneNumber1\": \"7975777666\",\n" +
            "      \"phoneNumber2\": \"\",\n" +
            "      \"countryCode1\": \"UK (44)\",\n" +
            "      \"countryCode2\": \"\"\n" +
            "    },\n" +
            "    \"details\": {\n" +
            "      \"name\": \"Mistos Mitsos Grande\",\n" +
            "      \"firstName\": \"Mistos Mitsos\",\n" +
            "      \"lastName\": \"Grande\",\n" +
            "      \"registrationNumber\": \"\",\n" +
            "      \"regNumTypeRadio\": 0,\n" +
            "      \"noRegistrationNumJustification\": \"\",\n" +
            "      \"countryOfBirth\": \"UK\"\n" +
            "    },\n" +
            "    \"type\": \"INDIVIDUAL\"\n" +
            "  },\n" +
            "  \"oldAccountHolder\": {\n" +
            "    \"id\": 1000018,\n" +
            "    \"emailAddress\": {\n" +
            "      \"emailAddress\": \"test_dp@mailinator.com\",\n" +
            "      \"emailAddressConfirmation\": \"test_dp@mailinator.com\"\n" +
            "    },\n" +
            "    \"address\": {\n" +
            "      \"country\": \"UK\",\n" +
            "      \"postCode\": \"SW1V 4LA\",\n" +
            "      \"townOrCity\": \"Unknown\",\n" +
            "      \"buildingAndStreet\": \"Chadwick Street\",\n" +
            "      \"buildingAndStreet2\": \"\",\n" +
            "      \"buildingAndStreet3\": \"\"\n" +
            "    },\n" +
            "    \"phoneNumber\": {\n" +
            "      \"phoneNumber1\": \"7975777666\",\n" +
            "      \"phoneNumber2\": \"\",\n" +
            "      \"countryCode1\": \"UK (44)\",\n" +
            "      \"countryCode2\": \"\"\n" +
            "    },\n" +
            "    \"details\": {\n" +
            "      \"name\": \"Mistos Mitsos Grande\",\n" +
            "      \"firstName\": \"Mistos Mitsos\",\n" +
            "      \"lastName\": \"Grande\",\n" +
            "      \"registrationNumber\": \"\",\n" +
            "      \"regNumTypeRadio\": 0,\n" +
            "      \"noRegistrationNumJustification\": \"\",\n" +
            "      \"countryOfBirth\": \"UK\"\n" +
            "    },\n" +
            "    \"type\": \"INDIVIDUAL\"\n" +
            "  },\n" +
            "  \"accountHolderContactInfo\": {\n" +
            "    \"primaryContact\": {\n" +
            "      \"id\": 16,\n" +
            "      \"details\": {\n" +
            "        \"firstName\": \"Mistos Mitsos\",\n" +
            "        \"lastName\": \"Grande\",\n" +
            "        \"aka\": \"\"\n" +
            "      },\n" +
            "      \"positionInCompany\": \"London\",\n" +
            "      \"address\": {\n" +
            "        \"country\": \"UK\",\n" +
            "        \"postCode\": \"SW1V 4LA\",\n" +
            "        \"townOrCity\": \"Unknown\",\n" +
            "        \"buildingAndStreet\": \"Chadwick Street\",\n" +
            "        \"buildingAndStreet2\": \"\",\n" +
            "        \"buildingAndStreet3\": \"\"\n" +
            "      },\n" +
            "      \"phoneNumber\": {\n" +
            "        \"phoneNumber1\": \"7975777666\",\n" +
            "        \"phoneNumber2\": \"\",\n" +
            "        \"countryCode1\": \"UK (44)\",\n" +
            "        \"countryCode2\": \"\"\n" +
            "      },\n" +
            "      \"emailAddress\": {\n" +
            "        \"emailAddress\": \"test_dp@mailinator.com\",\n" +
            "        \"emailAddressConfirmation\": \"test_dp@mailinator.com\"\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"oldAccountHolderContactInfo\": {\n" +
            "    \"primaryContact\": {\n" +
            "      \"id\": 16,\n" +
            "      \"details\": {\n" +
            "        \"firstName\": \"Mistos Mitsos\",\n" +
            "        \"lastName\": \"Grande\",\n" +
            "        \"aka\": \"\"\n" +
            "      },\n" +
            "      \"positionInCompany\": \"London\",\n" +
            "      \"address\": {\n" +
            "        \"country\": \"UK\",\n" +
            "        \"postCode\": \"SW1V 4LA\",\n" +
            "        \"townOrCity\": \"Unknown\",\n" +
            "        \"buildingAndStreet\": \"Chadwick Street\",\n" +
            "        \"buildingAndStreet2\": \"\",\n" +
            "        \"buildingAndStreet3\": \"\"\n" +
            "      },\n" +
            "      \"phoneNumber\": {\n" +
            "        \"phoneNumber1\": \"7975777666\",\n" +
            "        \"phoneNumber2\": \"\",\n" +
            "        \"countryCode1\": \"UK (44)\",\n" +
            "        \"countryCode2\": \"\"\n" +
            "      },\n" +
            "      \"emailAddress\": {\n" +
            "        \"emailAddress\": \"test_dp@mailinator.com\",\n" +
            "        \"emailAddressConfirmation\": \"test_dp@mailinator.com\"\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"accountDetails\": {\n" +
            "    \"name\": \"Admin account details\",\n" +
            "    \"address\": {\n" +
            "      \"country\": \"UK\",\n" +
            "      \"postCode\": \"SW1V 4LA\",\n" +
            "      \"townOrCity\": \"London\",\n" +
            "      \"buildingAndStreet\": \"line 1\",\n" +
            "      \"buildingAndStreet2\": \"line 2\",\n" +
            "      \"buildingAndStreet3\": \"line 3\"\n" +
            "    },\n" +
            "    \"billingContactDetails\": {\n" +
            "      \"contactName\": \"Ethnikos Star\",\n" +
            "      \"phoneNumberCountryCode\": \"GR (30)\",\n" +
            "      \"phoneNumber\": \"2134567891\",\n" +
            "      \"email\": \"test@email.com\",\n" +
            "      \"sopCustomerId\": \"test sop id\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"accountDetailsSameBillingAddress\": false,\n" +
            "  \"trustedAccountListRules\": {\n" +
            "    \"rule1\": false,\n" +
            "    \"rule2\": true\n" +
            "  },\n" +
            "  \"authorisedRepresentatives\": [\n" +
            "    {\n" +
            "      \"right\": \"INITIATE_AND_APPROVE\",\n" +
            "      \"urid\": \"UK405681794859\",\n" +
            "      \"firstName\": \"Representative 1\",\n" +
            "      \"lastName\": \"Authorized\",\n" +
            "      \"user\": {\n" +
            "        \"status\": \"ENROLLED\",\n" +
            "        \"firstName\": \"Representative 1\",\n" +
            "        \"lastName\": \"Authorized\",\n" +
            "        \"alsoKnownAs\": \"UK ETS Authorized Representative 1\"\n" +
            "      },\n" +
            "      \"contact\": {\n" +
            "        \"city\": \"ATHENS\",\n" +
            "        \"country\": \"GR\",\n" +
            "        \"emailAddress\": \"authorized-representative_1@trasys.gr\",\n" +
            "        \"line1\": \"authorized-representative_1 work building and street\",\n" +
            "        \"line2\": \"authorized-representative_1 work building and street optional\",\n" +
            "        \"line3\": \"authorized-representative_1 work building and street optional 2\",\n" +
            "        \"phoneNumber1\": \"2109006431\",\n" +
            "        \"countryCode1\": \"30\",\n" +
            "        \"postCode\": \"15231\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"right\": \"INITIATE_AND_APPROVE\",\n" +
            "      \"urid\": \"UK813935774586\",\n" +
            "      \"firstName\": \"User\",\n" +
            "      \"lastName\": \"Enrolled\",\n" +
            "      \"user\": {\n" +
            "        \"status\": \"ENROLLED\",\n" +
            "        \"firstName\": \"User\",\n" +
            "        \"lastName\": \"Enrolled\",\n" +
            "        \"alsoKnownAs\": \"UK ETS enrolled user\"\n" +
            "      },\n" +
            "      \"contact\": {\n" +
            "        \"city\": \"ATHENS\",\n" +
            "        \"country\": \"GR\",\n" +
            "        \"emailAddress\": \"enrolled_user@trasys.gr\",\n" +
            "        \"line1\": \"enrolled_user work building and street\",\n" +
            "        \"line2\": \"enrolled_user work building and street optional\",\n" +
            "        \"line3\": \"enrolled_user work building and street optional 2\",\n" +
            "        \"phoneNumber1\": \"21066188543\",\n" +
            "        \"countryCode1\": \"30\",\n" +
            "        \"postCode\": \"15231\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"changedAccountHolderId\": 1000018,\n" +
            "  \"governmentAccount\": false,\n" +
            "  \"kyotoAccountType\": false,\n" +
            "  \"transactionsAllowed\": false,\n" +
            "  \"canBeClosed\": false\n" +
            "}";

        User user = new User();
        user.setIamIdentifier("iamIdentifier");

        Task task = new Task();
        task.setRequestId(1000176L);
        task.setInitiatedDate(Timestamp.valueOf("2023-10-03 14:22:48.966"));
        task.setInitiatedBy(user);
        task.setDifference(difference);

        return task;
    }
}
