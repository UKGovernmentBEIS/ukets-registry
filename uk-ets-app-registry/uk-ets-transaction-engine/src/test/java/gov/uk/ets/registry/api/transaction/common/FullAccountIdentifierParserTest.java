package gov.uk.ets.registry.api.transaction.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FullAccountIdentifierParserTest {

    @Test
    public void test() {
        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance(null);
        assertTrue(parser.isEmpty());
    }

    @Test
    public void testRegistry() {
        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance("GB-100-12345-0-12");
        assertFalse(parser.isEmpty());
        assertTrue(parser.belongsToRegistry());
        assertTrue(parser.hasValidRegistryCode());

        parser = FullAccountIdentifierParser.getInstance("JP-100-12345");
        assertFalse(parser.isEmpty());
        assertFalse(parser.belongsToRegistry());
        assertTrue(parser.hasValidRegistryCode());

        parser = FullAccountIdentifierParser.getInstance("XX-100-12345");
        assertFalse(parser.isEmpty());
        assertFalse(parser.hasValidRegistryCode());

        parser = FullAccountIdentifierParser.getInstance("11-100-12345");
        assertFalse(parser.isEmpty());
        assertFalse(parser.hasValidRegistryCode());
    }

    @Test
    public void testAccountType() {
        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance("GB-100-12345-0-12");
        assertTrue(parser.hasValidType());

        parser = FullAccountIdentifierParser.getInstance("GB-999-12345-0-12");
        assertFalse(parser.hasValidType());

        parser = FullAccountIdentifierParser.getInstance("GB");
        assertFalse(parser.hasValidType());

        parser = FullAccountIdentifierParser.getInstance("GB-");
        assertFalse(parser.hasValidType());

        parser = FullAccountIdentifierParser.getInstance("GB-  ");
        assertFalse(parser.hasValidType());

        parser = FullAccountIdentifierParser.getInstance("GB-@@@-12345-0-12");
        assertFalse(parser.hasValidType());
    }

    @Test
    public void testPeriod() {
        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance("GB-100-12345-0-12");
        assertTrue(parser.hasCommitmentPeriod());
        assertTrue(parser.hasValidCommitmentPeriod());

        parser = FullAccountIdentifierParser.getInstance("GB-100-12345");
        assertFalse(parser.hasCommitmentPeriod());
        assertTrue(parser.hasValidCommitmentPeriod());

        parser = FullAccountIdentifierParser.getInstance("GB-100-12345-99");
        assertTrue(parser.hasCommitmentPeriod());
        assertFalse(parser.hasValidCommitmentPeriod());
    }

    @Test
    public void testCheckDigits() {



        FullAccountIdentifierParser parser = FullAccountIdentifierParser.getInstance("GB-100-12345-0");
        assertTrue(parser.hasCheckDigits());

        parser = FullAccountIdentifierParser.getInstance("GB-100-12345-0-1111");
        assertFalse(parser.hasCheckDigits());

        parser = FullAccountIdentifierParser.getInstance("GB-100-12345-0-xx");
        assertFalse(parser.hasCheckDigits());

        parser = FullAccountIdentifierParser.getInstance("GB-100-12345-0-22");
        assertTrue(parser.hasCheckDigits());
        assertFalse(parser.hasValidCheckDigits());

        parser = FullAccountIdentifierParser.getInstance("GB-100-12345-2-14");
        assertTrue(parser.hasValidCheckDigits());
    }

}