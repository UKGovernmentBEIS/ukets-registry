package gov.uk.ets.registry.api.tal;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import lombok.Builder;
import lombok.Getter;

public class TrustedAccountTestHelper {
    private EntityManager entityManager;

    public TrustedAccountTestHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TrustedAccount addTrustedAccount(AddTrustedAccountCommand command) {
        TrustedAccount trustedAccount = new TrustedAccount();
        trustedAccount.setAccount(command.getAccount());
        trustedAccount.setTrustedAccountFullIdentifier(command.getTrustedAccountFullIdentifier());
        trustedAccount.setStatus(command.getStatus());
        trustedAccount.setDescription(command.getDescription());
        trustedAccount.setActivationDate(command.getActivationDate());
        entityManager.persist(trustedAccount);
        return trustedAccount;
    }

    @Builder
    @Getter
    public static class AddTrustedAccountCommand {
        private Account account;
        private String trustedAccountFullIdentifier;
        private TrustedAccountStatus status;
        private String description;
        private LocalDateTime activationDate;
    }
}
