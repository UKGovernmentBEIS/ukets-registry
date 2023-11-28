package gov.uk.ets.reports.model.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class ReportGenerationCommandTest {

    @Test
    void sameIdAreEqualCommands() {
        Long id = 2L;
        ReportGenerationCommand cmd1 = ReportGenerationCommand.builder().id(id).build();
        ReportGenerationCommand cmd2 = ReportGenerationCommand.builder().id(id).build();

        assertEquals(cmd1, cmd2);
    }

    @Test
    void differentIdAreNotEqualCommands() {

        ReportGenerationCommand cmd1 = ReportGenerationCommand.builder().id(2L).build();
        ReportGenerationCommand cmd2 = ReportGenerationCommand.builder().id(3L).build();

        assertNotEquals(cmd1, cmd2);
    }
}
