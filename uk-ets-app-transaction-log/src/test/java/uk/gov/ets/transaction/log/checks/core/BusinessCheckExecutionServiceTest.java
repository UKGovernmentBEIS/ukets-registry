package uk.gov.ets.transaction.log.checks.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessCheckExecutionServiceTest {

    private BusinessCheckExecutionService executionService;
    private final CheckSuccessful check2 = new CheckSuccessful();
    private final CheckErrorManualCode check1 = new CheckErrorManualCode();
    private final CheckErrorAutomaticCode check3 = new CheckErrorAutomaticCode();
    private final CheckNoErrorCode check4 = new CheckNoErrorCode();
    private final CheckNoErrorMessage check5 = new CheckNoErrorMessage();

    BusinessCheckContext context = new BusinessCheckContext();

    @BeforeEach
    void setUp() {
        executionService = new BusinessCheckExecutionService();
        context = new BusinessCheckContext();
    }

    @Test
    void executeChecks() {
        context.reset();
        assertTrue(executionService.execute(context, check2).success());

        context.reset();
        assertFalse(executionService.execute(context, check3, check2, check1).success());

        context.reset();
        assertFalse(executionService.execute(context, check2, check1, check3).success());

        context.reset();
        assertFalse(executionService.execute(context, check3, check2, check1).success());

        context.reset();
        assertFalse(executionService.execute(context, check2, check1).success());

        context.reset();
        assertFalse(executionService.execute(context, check1, check2).success());

        context.reset();
        assertTrue(executionService.execute(context, check2).success());

        context.reset();
        List<BusinessCheckError> errors = executionService.execute(context, check3, check2, check1).getErrors();

        assertEquals(1, errors.size());

        context.reset();
        errors = executionService.execute(context, check2, check1).getErrors();

        assertEquals(1, errors.size());

        context.reset();
        errors = executionService.execute(context, check1).getErrors();

        assertEquals(1, errors.size());
        assertNotNull(errors.get(0).getCode());
        assertNotNull(errors.get(0).getMessage());

        context.reset();
        assertThrows(IllegalArgumentException.class, () -> {
           executionService.execute(context, check4);
        });

        context.reset();
        assertThrows(IllegalArgumentException.class, () -> {
            executionService.execute(context, check5);
        });

    }

    @Test
    void testBusinessCheckError() {
        BusinessCheckError error1 = new BusinessCheckError(1, "Message 1");
        BusinessCheckError error2 = new BusinessCheckError(2, "Message 2");

        assertEquals(2, Set.of(error1, error2).size());
        assertEquals(2, Map.of(error1, error1.toString(), error2, error2.toString()).size());

        error2.setMessage("Message 1");

        assertEquals(2, Set.of(error1, error2).size());
        assertEquals(2, Map.of(error1, error1.toString(), error2, error2.toString()).size());

        error2.setCode(1);
        error2.setMessage("Message 2");

        assertEquals(1, new HashSet<>(Arrays.asList(error1, error2)).size());
        assertEquals(1, new HashMap<>() {{
            put(error1, error1.toString());
            put(error2, error2.toString());
        }}.size());
    }

    @Test
    void testBusinessCheckResult() {
        BusinessCheckResult result = new BusinessCheckResult();
        assertTrue(result.success());

        result.setErrors(new ArrayList<>());
        assertTrue(result.success());

        result.setErrors(Arrays.asList(new BusinessCheckError(1, "Error Message")));
        assertFalse(result.success());
        assertEquals(1, result.getErrors().size());
        assertNotNull(result.toString());

        assertFalse(new BusinessCheckResult(Arrays.asList(new BusinessCheckError(1, "Error Message")))
            .success());
    }

}