package gov.uk.ets.registry.api.payment.domain;

import gov.uk.ets.registry.api.payment.domain.types.PaymentHistoryType;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

/**
 * Represents a payment history.
 */

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class PaymentHistory implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "payment_history_id_generator", sequenceName = "payment_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_history_id_generator")
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
    private BigDecimal amount;

    /**
     * The type.
     */
    @Enumerated(EnumType.STRING)
    private PaymentHistoryType type;

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

    // Hibernate-specific view of the numeric column as text
    @Formula("reference_number::text")
    private String referenceNumberText;

    @PrePersist
    void initDefaults() {
        this.type = PaymentHistoryType.PAYMENT;
        this.created = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updated = LocalDateTime.now();
    }
}
