package gov.uk.ets.registry.api.transaction.checks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessCheckExecutionServiceTest {

    private BusinessCheckExecutionService executionService;
    private final CheckSuccessful check2 = new CheckSuccessful();
    private final CheckErrorManualCode check1 = new CheckErrorManualCode();
    private final CheckErrorAutomaticCode check3 = new CheckErrorAutomaticCode();

    BusinessCheckContext context = new BusinessCheckContext();

    @BeforeEach
    void setUp() {
        executionService = new BusinessCheckExecutionService();
        context = new BusinessCheckContext();
    }

    @Test
    void executeThrowsSuccess() {
        context.reset();
        assertDoesNotThrow(() ->
            executionService.executeThrows(context, Arrays.asList(check2)));

        context.reset();
        assertDoesNotThrow(() ->
            executionService.executeThrows(context, Arrays.asList(check3, check2, check1), BusinessCheckGroup.ACCOUNT));

        context.reset();
        assertDoesNotThrow(() ->
            executionService.executeThrows(context, Arrays.asList(check2, check1, check3), BusinessCheckGroup.ACCOUNT));

        context.reset();
        assertDoesNotThrow(() ->
            executionService.executeThrows(context, Arrays.asList(check3, check2, check1), BusinessCheckGroup.ACCOUNT));

        context.reset();
        assertDoesNotThrow(() ->
            executionService.executeThrows(context, Arrays.asList(check2, check1), BusinessCheckGroup.ACCOUNT));

        context.reset();
        assertDoesNotThrow(() ->
            executionService.executeThrows(context, Arrays.asList(check1, check2), BusinessCheckGroup.ACCOUNT));

        context.reset();
        assertDoesNotThrow(() ->
            executionService.executeThrows(context, Arrays.asList(check2), BusinessCheckGroup.ACCOUNT));
    }

    @Test
    void executeSuccess() {
        context.reset();

        assertDoesNotThrow(() ->
            assertTrue(executionService.execute(context, Arrays.asList(check2)).success()));

        context.reset();
        assertDoesNotThrow(() ->
            assertTrue(executionService.execute(context, Arrays.asList(check3, check2, check1), BusinessCheckGroup.ACCOUNT).success()));

        context.reset();
        assertDoesNotThrow(() ->
            assertTrue(executionService.execute(context, Arrays.asList(check2, check1, check3), BusinessCheckGroup.ACCOUNT).success()));

        context.reset();
        assertDoesNotThrow(() ->
            assertTrue(executionService.execute(context, Arrays.asList(check3, check2, check1), BusinessCheckGroup.ACCOUNT).success()));

        context.reset();
        assertDoesNotThrow(() ->
            assertTrue(executionService.execute(context, Arrays.asList(check2, check1), BusinessCheckGroup.ACCOUNT).success()));

        context.reset();
        assertDoesNotThrow(() ->
            assertTrue(executionService.execute(context, Arrays.asList(check1, check2), BusinessCheckGroup.ACCOUNT).success()));

        context.reset();
        assertDoesNotThrow(() ->
            assertTrue(executionService.execute(context, Arrays.asList(check2), BusinessCheckGroup.ACCOUNT).success()));
    }

    @Test
    void executeThrowsFails() {

        context.reset();
        BusinessCheckError error = assertThrows(
            BusinessCheckException.class, () ->
                executionService.executeThrows(context, Arrays.asList(check1))).getErrors().get(0);

        assertEquals(1, error.getCode());
        assertEquals("Some message", error.getMessage());

        context.reset();
        error = assertThrows(
            BusinessCheckException.class, () ->
                executionService.executeThrows(context, Arrays.asList(check2, check1))).getErrors().get(0);

        assertEquals(1, error.getCode());
        assertEquals("Some message", error.getMessage());

        context.reset();
        error = assertThrows(
            BusinessCheckException.class, () ->
                executionService.executeThrows(context, Arrays.asList(check3, check1))).getErrors().get(0);

        assertEquals(3, error.getCode());
        assertEquals("Message", error.getMessage());

        context.reset();
        error = assertThrows(
            BusinessCheckException.class, () ->
                executionService.executeThrows(context, Arrays.asList(check3, check2, check1))).getErrors().get(0);

        assertEquals(3, error.getCode());
        assertEquals("Message", error.getMessage());

    }

    @Test
    void executeThrowsFailsAllowManyErrors() {
        context.reset();
        List<BusinessCheckError> errors = assertThrows(
            BusinessCheckException.class, () ->
                executionService.executeThrows(context, Arrays.asList(check3, check2, check1), null, false)).getErrors();

        assertEquals(2, errors.size());

        context.reset();
        errors = assertThrows(
            BusinessCheckException.class, () ->
                executionService.executeThrows(context, Arrays.asList(check2, check1), null, false)).getErrors();

        assertEquals(1, errors.size());

        context.reset();
        errors = assertThrows(
            BusinessCheckException.class, () ->
                executionService.executeThrows(context, Arrays.asList(check1), null, false)).getErrors();

        assertEquals(1, errors.size());
    }

}