package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

/**
 *
 * @author gkountak
 */
public class FirstTrack2TransferEntry {

    private String transactionId;

    private String registry;

    private Long quantity;


    /**
     *
     */
    public FirstTrack2TransferEntry() {
        // TODO Auto-generated constructor stub
    }




    /**
     * @param registry
     * @param year
     * @param quantity
     */
    public FirstTrack2TransferEntry(String transactionId, String registry, Long quantity) {
        super();
        this.transactionId = transactionId;
        this.registry = registry;
        this.quantity = quantity;
    }




    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }




    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }




    /**
     * @return the registry
     */
    public String getRegistry() {
        return registry;
    }


    /**
     * @param registry the registry to set
     */
    public void setRegistry(String registry) {
        this.registry = registry;
    }


    /**
     * @return the quantity
     */
    public Long getQuantity() {
        return quantity;
    }


    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
