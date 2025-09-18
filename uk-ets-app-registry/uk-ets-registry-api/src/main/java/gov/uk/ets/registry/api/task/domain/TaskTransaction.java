package gov.uk.ets.registry.api.task.domain;

import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import gov.uk.ets.registry.api.transaction.domain.SearchableTransaction;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"task","transactionIdentifier"})
public class TaskTransaction implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "task_transaction_id_generator", sequenceName = "task_transaction_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_transaction_id_generator")
    private Long id;
    
    /**
     * The request business identifier.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Column(name = "transaction_identifier")
    private String transactionIdentifier;
    
    /**
     * The recipient account number.
     */
    @Column(name = "recipient_account_number")
    @Convert(converter = StringTrimConverter.class)
    private String recipientAccountNumber;
    
    @OneToOne
    @JoinColumn(name = "transaction_identifier",
        referencedColumnName = "identifier",
        insertable = false,
        updatable = false,
        foreignKey = @jakarta.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private SearchableTransaction transaction;

}
