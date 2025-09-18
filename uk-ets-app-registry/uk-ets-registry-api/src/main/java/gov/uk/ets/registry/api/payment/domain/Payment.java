package gov.uk.ets.registry.api.payment.domain;

import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a payment.
 */

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Payment implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 1417528596248002730L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "payment_id_generator", sequenceName = "payment_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_id_generator")
    private Long id;

    /**
     * The request business identifier.
     */
    @Column(name = "reference_number")
    private Long referenceNumber;
    
    /**
     * The key of the payment in the GOV.UK Pay service.
     */
    @Column(name = "payment_id")
    private String paymentId;
    
    /**
     * The requested amount to pay.
     */
    @Column(name = "amount_requested")
    private BigDecimal amountRequested;
    
    /**
     * The actually paid amount.
     */
    @Column(name = "amount_paid")
    private BigDecimal amountPaid;
    
    /**
     * The description of the payment.
     */
    @Column(name = "description")
    private String description;
    
    /**
     * The url suffix of the web payment link.
     */
    @Column(name = "url_suffix")
    private String urlSuffix;
    
    /**
     * The method.
     */
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    
    /**
     * The payment status.
     */
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    /**
     * When this payment was created.
     */
    @Column(name = "created", columnDefinition = "TIMESTAMP")
    private LocalDateTime created;
    
    /**
     * When this payment was last updated.
     */
    @Column(name = "updated", columnDefinition = "TIMESTAMP")
    private LocalDateTime updated;

    /**
     * When this payment was Actually paid.
     */
    @Column(name = "paid_on", columnDefinition = "TIMESTAMP")
    private LocalDate paidOn;
    /**
     * When this payment was Actually paid.
     */
    @Column(name = "paid_by")
    private String paidBy;
    
    @PrePersist
    void initDefaults() {
        this.urlSuffix = UUID.randomUUID().toString();
        this.created = LocalDateTime.now();
    }
    
    @PreUpdate
    void onUpdate() {
        this.updated = LocalDateTime.now();
    }
}
