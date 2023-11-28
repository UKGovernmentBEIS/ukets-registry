package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
