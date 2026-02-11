package gov.uk.ets.registry.api.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {
        "account",
        "accountHolderRepresentative"
})
@Entity
@Table(
        name = "account_representative_invitation",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "account_id",
                                "account_holder_representative_id"
                        }
                )
        }
)
public class AccountRepresentativeInvitation implements Serializable {

    private static final long serialVersionUID = -4512389765432109825L;

    @Id
    @SequenceGenerator(name = "account_rep_inv_id_generator", sequenceName = "account_rep_inv_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_rep_inv_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_holder_representative_id", nullable = false)
    private AccountHolderRepresentative accountHolderRepresentative;

    @Column(name = "invited_date")
    private LocalDateTime invitedDate;
}
