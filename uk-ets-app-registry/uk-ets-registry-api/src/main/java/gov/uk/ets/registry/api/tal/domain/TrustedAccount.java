package gov.uk.ets.registry.api.tal.domain;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Trusted Account relationship.
 */
@Entity
@Table(name = "trusted_account")
@Getter
@Setter
public class TrustedAccount implements Serializable {
    private static final long serialVersionUID = -2055839378986003392L;
    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "trusted_account_id_generator", sequenceName = "trusted_account_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trusted_account_id_generator")
    private Long id;

    /**
     * The owner of the trusted account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * The full identifier of the trusted account.
     */
    @Column(name = "trusted_account_full_identifier")
    private String trustedAccountFullIdentifier;

    /**
     * The description of the trusted account.
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TrustedAccountStatus status;

    /**
     * The description of the trusted account.
     */
    @Column(name = "description")
    @Convert(converter = StringTrimConverter.class)
    private String description;

    /**
     * The description of the trusted account.
     */
    @Column(name = "activation_date")
    private LocalDateTime activationDate;
}
