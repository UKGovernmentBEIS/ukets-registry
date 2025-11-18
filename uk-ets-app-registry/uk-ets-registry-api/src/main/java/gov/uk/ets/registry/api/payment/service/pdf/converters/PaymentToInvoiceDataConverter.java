package gov.uk.ets.registry.api.payment.service.pdf.converters;

import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class PaymentToInvoiceDataConverter {

    private static final String VAT = "0.00";

    public List<String[]> convert(PaymentDTO paymentDTO) {
        return convert(List.of(paymentDTO));
    }

    public List<String[]> convert(List<PaymentDTO> payments) {
        List<String[]> list = new ArrayList<>();
        payments.forEach(payment -> {
            list.add(new String[]{payment.getDescription(),
                    twoDecimalFormatString(payment.getAmount()),
                    VAT,
                    twoDecimalFormatString(payment.getAmount())});
        });
        return list;
    }

    public List<String[]> convert(Payment payment) {
        return convertPayments(List.of(payment));
    }

    public List<String[]> convertPayments(List<Payment> payments) {
        List<String[]> list = new ArrayList<>();
        payments.forEach(payment -> {
            list.add(new String[]{payment.getDescription(),
                    twoDecimalFormatString(payment.getAmountPaid()),
                    VAT,
                    twoDecimalFormatString(payment.getAmountPaid())});
        });
        return list;
    }

    public String total(List<PaymentDTO> payments) {
        BigDecimal total = payments.stream()
                .map(PaymentDTO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return twoDecimalFormatString(total);
    }

    public String total(PaymentDTO payment) {
        return twoDecimalFormatString(payment.getAmount());
    }

    public String total(Payment payment) {
        return twoDecimalFormatString(payment.getAmountPaid());
    }

    private String twoDecimalFormatString(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format(Locale.UK, "%.2f", amount);
    }
}
