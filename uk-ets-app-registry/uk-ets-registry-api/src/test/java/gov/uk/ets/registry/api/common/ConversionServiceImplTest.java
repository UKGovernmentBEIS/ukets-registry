package gov.uk.ets.registry.api.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConversionServiceImplTest {

    private ConversionService conversionService = new ConversionServiceImpl();

    @Test
    void convertByteAmountToHumanReadable() {

        assertTrue(conversionService.convertByteAmountToHumanReadable(0).endsWith("B"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(27).endsWith("B"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(999).endsWith("B"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(1000).endsWith("B"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(1023).endsWith("B"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(1024).endsWith("kB"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(1728).endsWith("kB"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(110592).endsWith("kB"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(7077888).endsWith("MB"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(452984832).endsWith("MB"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(28991029248L).endsWith("GB"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(1855425871872L).endsWith("TB"));
        assertTrue(conversionService.convertByteAmountToHumanReadable(9223372036854775807L).endsWith("EB"));
    }
}
