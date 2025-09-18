package gov.uk.ets.registry.api.payment.service.pdf.converters;

import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentToInvoiceDataConverterTest {

    private PaymentToInvoiceDataConverter converter;

    @BeforeEach
    void setUp() {
        converter = new PaymentToInvoiceDataConverter();
    }

    @Test
    void testConvertSinglePayment() {
        PaymentDTO payment = new PaymentDTO();
        payment.setDescription("CO2 Permit");
        payment.setAmount(new BigDecimal("123.45"));

        List<String[]> result = converter.convert(payment);

        assertEquals(1, result.size());
        String[] row = result.get(0);
        assertArrayEquals(
                new String[]{"CO2 Permit", "123.45", "0.00", "123.45"},
                row
        );
    }

    @Test
    void testConvertMultiplePayments() {
        PaymentDTO p1 = new PaymentDTO();
        p1.setDescription("PAYMENT 1");
        p1.setAmount(new BigDecimal("10"));

        PaymentDTO p2 = new PaymentDTO();
        p2.setDescription("PAYMENT 2");
        p2.setAmount(new BigDecimal("20.50"));

        List<String[]> result = converter.convert(List.of(p1, p2));

        assertEquals(2, result.size());

        assertArrayEquals(new String[]{"PAYMENT 1", "10.00", "0.00", "10.00"}, result.get(0));
        assertArrayEquals(new String[]{"PAYMENT 2", "20.50", "0.00", "20.50"}, result.get(1));
    }

    @Test
    void testTotalWithSinglePayment() {
        PaymentDTO payment = new PaymentDTO();
        payment.setAmount(new BigDecimal("123.456"));

        String result = converter.total(payment);

        assertEquals("123.46", result); // rounding applied
    }

    @Test
    void testTotalWithMultiplePayments() {
        PaymentDTO p1 = new PaymentDTO();
        p1.setAmount(new BigDecimal("10.1"));

        PaymentDTO p2 = new PaymentDTO();
        p2.setAmount(new BigDecimal("20.22"));

        String result = converter.total(List.of(p1, p2));

        assertEquals("30.32", result);
    }

    @Test
    void testTotalHandlesNullAmounts() {
        PaymentDTO p1 = new PaymentDTO();
        p1.setAmount(null);

        PaymentDTO p2 = new PaymentDTO();
        p2.setAmount(new BigDecimal("19.99"));

        String result = converter.total(List.of(p1, p2));

        assertEquals("19.99", result);
    }

    @Test
    void testFormatHandlesNull() {
        PaymentDTO payment = new PaymentDTO();
        payment.setAmount(null);

        String result = converter.total(payment);

        assertEquals("0.00", result);
    }
}
