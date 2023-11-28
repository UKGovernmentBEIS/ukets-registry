package gov.uk.ets.reports.model;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import gov.uk.ets.reports.model.criteria.AccountSearchCriteria;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;


@JsonTest
public class ReportGenerationCommandDeserializationTest {

    @Autowired
    private JacksonTester<ReportGenerationCommand> json;

    @Test
    public void shouldDeserializeCriteriaByReportType() throws IOException {
        //language=JSON
        String content = "{\"id\": 1, \"type\": \"R0001\"}";

        ReportGenerationCommand reportGenerationCommand = json.parseObject(content);

        assertThat(reportGenerationCommand.getId()).isEqualTo(1);
        assertThat(reportGenerationCommand.getType()).isEqualTo(ReportType.R0001);
    }

    @Test
    public void shouldThrowOnNonExistentReportType() {
        //language=JSON
        String content = "{\"id\": 1, \"type\": \"WRONG\"}";

        InvalidFormatException ife = 
        		assertThrows(InvalidFormatException.class, () -> json.parseObject(content));
        assertTrue(ife.getMessage().contains("Cannot deserialize value of type `gov.uk.ets.reports.model.ReportType` "
        		+ "from String \"WRONG\": not one of the values accepted for Enum class"));
    }
}
