package gov.uk.ets.registry.api.payment.service.pdf;

public class PaymentInvoicePdfUtils {
    static final Integer LOGO_WIDTH = 180;
    static final Integer LOGO_HEIGHT = 90;
    static final String LOGO_IMAGE_PATH = "images/ea_logo.png";
    public static final String CURRENCY = "£";
    public static final String TOTAL = "Total to Pay";
    public static final String INVOICE_TITLE = "PAYMENT Request";
    public static final String RECEIPT_TITLE = "Receipt of Payment";
    public static final String COLUMN_1 = "Description";
    public static final String COLUMN_2 = "Fee ("+CURRENCY+")";
    public static final String COLUMN_3 = "VAT";
    public static final String COLUMN_4 = "Total";
    public static final String VAT_NO = "VAT No: 662490134";
    public static final String NA = "N/A";
    public static final String PAYMENT_REFERENCE = "PAYMENT Reference: ";
    public static final String RECEIPT_FOR_INFO = "PAYMENT has been received for the following:";

    private PaymentInvoicePdfUtils() {
        // prevent instantiation
    }
}
