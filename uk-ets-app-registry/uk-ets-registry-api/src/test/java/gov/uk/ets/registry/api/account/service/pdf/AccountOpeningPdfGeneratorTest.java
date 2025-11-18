package gov.uk.ets.registry.api.account.service.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import gov.uk.ets.registry.api.common.CountryMap;
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
    private final CountryMap countryMap = new CountryMap(mapper);
    private final DocumentHelper helper = new DocumentHelper(mapper, countryMap);
    private final HeaderAndFooterPageEventHelper headerAndFooterPageEventHelper = new HeaderAndFooterPageEventHelper(helper);
    @MockBean
    private UserService userService;

    private AccountOpeningPdfGenerator accountOpeningPdfGenerator;

    @BeforeEach
    public void setup() {
        accountOpeningPdfGenerator = new AccountOpeningPdfGenerator(userService, mapper, helper, headerAndFooterPageEventHelper);
        ReflectionTestUtils.setField(countryMap, "countriesResource", new ClassPathResource("data/countries.json"));
        countryMap.init();
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
                "  \"accountType\": \"OPERATOR_HOLDING_ACCOUNT\",\n" +
                "  \"accountHolder\": {\n" +
                "    \"id\": 1000013,\n" +
                "    \"emailAddress\": {\n" +
                "      \"emailAddress\": \"haris-admin@mailinator.com\",\n" +
                "      \"emailAddressConfirmation\": \"haris-admin@mailinator.com\"\n" +
                "    },\n" +
                "    \"address\": {\n" +
                "      \"country\": \"GR\",\n" +
                "      \"postCode\": \"17237\",\n" +
                "      \"townOrCity\": \"Athens\",\n" +
                "      \"buildingAndStreet\": \"Whatever\",\n" +
                "      \"buildingAndStreet2\": \"\",\n" +
                "      \"buildingAndStreet3\": \"\",\n" +
                "      \"stateOrProvince\": \"\"\n" +
                "    },\n" +
                "    \"phoneNumber\": {\n" +
                "      \"phoneNumber1\": \"6999995229\",\n" +
                "      \"phoneNumber2\": \"6999995229\",\n" +
                "      \"countryCode1\": \"GR (30)\",\n" +
                "      \"countryCode2\": \"GR (30)\"\n" +
                "    },\n" +
                "    \"details\": {\n" +
                "      \"name\": \"Haris Bouchlis\",\n" +
                "      \"firstName\": \"Haris\",\n" +
                "      \"lastName\": \"Bouchlis\",\n" +
                "      \"registrationNumber\": \"\",\n" +
                "      \"regNumTypeRadio\": 0,\n" +
                "      \"noRegistrationNumJustification\": \"\",\n" +
                "      \"countryOfBirth\": \"GR\"\n" +
                "    },\n" +
                "    \"type\": \"INDIVIDUAL\"\n" +
                "  },\n" +
                "  \"oldAccountHolder\": {\n" +
                "    \"id\": 1000013,\n" +
                "    \"emailAddress\": {\n" +
                "      \"emailAddress\": \"haris-admin@mailinator.com\",\n" +
                "      \"emailAddressConfirmation\": \"haris-admin@mailinator.com\"\n" +
                "    },\n" +
                "    \"address\": {\n" +
                "      \"country\": \"GR\",\n" +
                "      \"postCode\": \"17237\",\n" +
                "      \"townOrCity\": \"Athens\",\n" +
                "      \"buildingAndStreet\": \"Whatever\",\n" +
                "      \"buildingAndStreet2\": \"\",\n" +
                "      \"buildingAndStreet3\": \"\",\n" +
                "      \"stateOrProvince\": \"\"\n" +
                "    },\n" +
                "    \"phoneNumber\": {\n" +
                "      \"phoneNumber1\": \"6999995229\",\n" +
                "      \"phoneNumber2\": \"6999995229\",\n" +
                "      \"countryCode1\": \"GR (30)\",\n" +
                "      \"countryCode2\": \"GR (30)\"\n" +
                "    },\n" +
                "    \"details\": {\n" +
                "      \"name\": \"Haris Bouchlis\",\n" +
                "      \"firstName\": \"Haris\",\n" +
                "      \"lastName\": \"Bouchlis\",\n" +
                "      \"registrationNumber\": \"\",\n" +
                "      \"regNumTypeRadio\": 0,\n" +
                "      \"noRegistrationNumJustification\": \"\",\n" +
                "      \"countryOfBirth\": \"GR\"\n" +
                "    },\n" +
                "    \"type\": \"INDIVIDUAL\"\n" +
                "  },\n" +
                "  \"accountHolderContactInfo\": {\n" +
                "    \"primaryContact\": {\n" +
                "      \"id\": 12,\n" +
                "      \"details\": { \"firstName\": \"Haris\", \"lastName\": \"Bouchlis\", \"aka\": \"\" },\n" +
                "      \"positionInCompany\": \"CIO\",\n" +
                "      \"address\": {\n" +
                "        \"country\": \"GR\",\n" +
                "        \"postCode\": \"17237\",\n" +
                "        \"townOrCity\": \"Athens\",\n" +
                "        \"buildingAndStreet\": \"Whatever\",\n" +
                "        \"buildingAndStreet2\": \"\",\n" +
                "        \"buildingAndStreet3\": \"\",\n" +
                "        \"stateOrProvince\": \"\"\n" +
                "      },\n" +
                "      \"phoneNumber\": {\n" +
                "        \"phoneNumber1\": \"6999995229\",\n" +
                "        \"phoneNumber2\": \"6999995229\",\n" +
                "        \"countryCode1\": \"GR (30)\",\n" +
                "        \"countryCode2\": \"GR (30)\"\n" +
                "      },\n" +
                "      \"emailAddress\": {\n" +
                "        \"emailAddress\": \"haris-admin@mailinator.com\",\n" +
                "        \"emailAddressConfirmation\": \"haris-admin@mailinator.com\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"oldAccountHolderContactInfo\": {\n" +
                "    \"primaryContact\": {\n" +
                "      \"id\": 12,\n" +
                "      \"details\": { \"firstName\": \"Haris\", \"lastName\": \"Bouchlis\", \"aka\": \"\" },\n" +
                "      \"positionInCompany\": \"CIO\",\n" +
                "      \"address\": {\n" +
                "        \"country\": \"GR\",\n" +
                "        \"postCode\": \"17237\",\n" +
                "        \"townOrCity\": \"Athens\",\n" +
                "        \"buildingAndStreet\": \"Whatever\",\n" +
                "        \"buildingAndStreet2\": \"\",\n" +
                "        \"buildingAndStreet3\": \"\",\n" +
                "        \"stateOrProvince\": \"\"\n" +
                "      },\n" +
                "      \"phoneNumber\": {\n" +
                "        \"phoneNumber1\": \"6999995229\",\n" +
                "        \"phoneNumber2\": \"6999995229\",\n" +
                "        \"countryCode1\": \"GR (30)\",\n" +
                "        \"countryCode2\": \"GR (30)\"\n" +
                "      },\n" +
                "      \"emailAddress\": {\n" +
                "        \"emailAddress\": \"haris-admin@mailinator.com\",\n" +
                "        \"emailAddressConfirmation\": \"haris-admin@mailinator.com\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"accountDetails\": {\n" +
                "    \"name\": \"Subtask\",\n" +
                "    \"salesContactDetails\": {},\n" +
                "    \"excludedFromBilling\": false,\n" +
                "    \"accountDetailsSameBillingAddress\": false\n" +
                "  },\n" +
                "  \"accountDetailsSameBillingAddress\": false,\n" +
                "  \"trustedAccountListRules\": { \"rule1\": true, \"rule2\": false, \"rule3\": true },\n" +
                "  \"operator\": {\n" +
                "    \"type\": \"INSTALLATION\",\n" +
                "    \"name\": \"Haris Subtask\",\n" +
                "    \"regulator\": \"DAERA\",\n" +
                "    \"firstYear\": 2021,\n" +
                "    \"activityTypes\": [\"STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_2009_31_EC\"],\n" +
                "    \"permit\": { \"id\": \"123456789102255\" }\n" +
                "  },\n" +
                "  \"authorisedRepresentatives\": [\n" +
                "    {\n" +
                "      \"right\": \"INITIATE_AND_APPROVE\",\n" +
                "      \"urid\": \"UK600543316240\",\n" +
                "      \"firstName\": \"Representative 1 of Verifier\",\n" +
                "      \"lastName\": \"Authorized\",\n" +
                "      \"user\": {\n" +
                "        \"status\": \"ENROLLED\",\n" +
                "        \"firstName\": \"Representative 1 of Verifier\",\n" +
                "        \"lastName\": \"Authorized\"\n" +
                "      },\n" +
                "      \"contact\": {\n" +
                "        \"city\": \"ATHENS\",\n" +
                "        \"country\": \"GR\",\n" +
                "        \"emailAddress\": \"verifier_1@trasys.gr\",\n" +
                "        \"line1\": \"verifier_1 work building and street\",\n" +
                "        \"line2\": \"verifier_1 work building and street optional\",\n" +
                "        \"line3\": \"verifier_1 work building and street optional 2\",\n" +
                "        \"phoneNumber1\": \"2109806466\",\n" +
                "        \"countryCode1\": \"30\",\n" +
                "        \"postCode\": \"15231\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"changedAccountHolderId\": 1000013,\n" +
                "  \"governmentAccount\": false,\n" +
                "  \"kyotoAccountType\": false,\n" +
                "  \"transactionsAllowed\": false,\n" +
                "  \"canBeClosed\": false\n" +
                "}\n";
        User user = new User();
        user.setIamIdentifier("iamIdentifier");

        Task task = new Task();
        task.setRequestId(1000267L);
        task.setInitiatedDate(Timestamp.valueOf("2023-11-22 14:41:00.377"));
        task.setInitiatedBy(user);
        task.setDifference(difference);

        return task;
    }
}
