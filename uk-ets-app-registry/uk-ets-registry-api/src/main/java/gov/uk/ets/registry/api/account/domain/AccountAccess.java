package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.ToString;

@Entity
@Table(name = "account_access")
@Getter
@Setter
@ToString
public class AccountAccess implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -2458527841764451400L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "account_access_generator", sequenceName = "account_access_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_access_generator")
    private Long id;

    /**
     * The user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * The current state.
     */
    @Enumerated(EnumType.STRING)
    private AccountAccessState state;

    /**
     * The right.
     */
    @Column(name = "access_right")
    @Enumerated(EnumType.STRING)
    private AccountAccessRight right;
}
