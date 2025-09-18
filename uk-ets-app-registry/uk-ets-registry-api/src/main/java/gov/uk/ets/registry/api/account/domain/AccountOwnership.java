package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
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
import lombok.ToString;

@Table(name = "account_ownership")
@Entity
@Getter
@Setter
@ToString
@SequenceGenerator(name = "account_ownership_id_generator", sequenceName = "account_ownership_seq", allocationSize = 1)
public class AccountOwnership {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_ownership_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @ToString.Exclude
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id")
    @ToString.Exclude
    private AccountHolder holder;

    @Enumerated(EnumType.STRING)
    private AccountOwnershipStatus status;

    @Column(name = "date_of_ownership")
    private LocalDateTime dateOfOwnership;
}
