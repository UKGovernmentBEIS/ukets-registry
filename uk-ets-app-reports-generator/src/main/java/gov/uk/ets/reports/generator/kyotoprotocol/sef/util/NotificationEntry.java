package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLNotificationTypeEnum;

/**
 *
 * @author gkountak
 */
public class NotificationEntry {

    private String notificationId;

    private short year;

    private String registry;

    private String notificationTypeCode;

    private Long quantity;

    /**
     *
     */
    public NotificationEntry() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the notificationId
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * @param notificationId the notificationId to set
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
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
     * @return the notificationTypeCode
     */
    public String getNotificationTypeCode() {
        return notificationTypeCode;
    }

    /**
     * @param notificationTypeCode the notificationTypeCode to set
     */
    public void setNotificationTypeCode(String notificationTypeCode) {
        this.notificationTypeCode = notificationTypeCode;
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

    /**
     * @return the year
     */
    public short getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(short year) {
        this.year = year;
    }

    /**
     * Gets the {@link ITLNotificationTypeEnum} from a short
     * @return
     */
    public ITLNotificationTypeEnum getITLNotificationTypeCode() {
        return ITLNotificationTypeEnum.getFromCode(this.notificationTypeCode);
    }

}
