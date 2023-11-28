package gov.uk.ets.reports.model.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class ReportGenerationEventTest {

    @Test
    void sameIdAreEqualEvents() {
    	Long id = 2L;
    	ReportGenerationEvent evt1 = new ReportGenerationEvent();
    	evt1.setId(id);
    	ReportGenerationEvent evt2 = new ReportGenerationEvent();
    	evt2.setId(id);
    	
        assertEquals(evt1,evt2);
    }
    
    @Test
    void differentIdAreNotEqualEvents() {
    	
    	ReportGenerationEvent evt1 = new ReportGenerationEvent();
    	evt1.setId(2L);
    	ReportGenerationEvent evt2 = new ReportGenerationEvent();
    	evt2.setId(3L);
    	
        assertNotEquals(evt1,evt2);
    }
}
