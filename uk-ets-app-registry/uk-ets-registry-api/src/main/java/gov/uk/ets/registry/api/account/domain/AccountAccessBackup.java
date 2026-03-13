package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_access_backup")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountAccessBackup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "account_access_backup_generator", sequenceName = "account_access_backup_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_access_backup_generator")
    private Long id;

    /**
     * The original AccountAccess ID.
     */
    @Column(name = "original_access_id", nullable = false)
    private Long originalAccessId;

    /**
     * The associated user ID.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The associated account ID.
     */
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /**
     * The state at the time of backup.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private AccountAccessState state;

    /**
     * The access right at the time of backup.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "access_right")
    private AccountAccessRight right;

    /**
     * Timestamp when backup was taken.
     */
    @Column(name = "backup_date", nullable = false)
    private LocalDateTime backupDate;
}
