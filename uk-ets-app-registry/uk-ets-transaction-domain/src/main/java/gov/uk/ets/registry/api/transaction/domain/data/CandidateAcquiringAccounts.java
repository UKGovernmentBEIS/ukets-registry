package gov.uk.ets.registry.api.transaction.domain.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Needed by the transaction proposal wizard.
 */
@Getter
@Setter
@Builder
public class CandidateAcquiringAccounts {

    /**
     * The transferring account Id.
     */
    private Long accountId;

    private boolean isCandidateListPredefined;

    /**
     * Trusted accounts under the same account holder.
     */
    private List<AcquiringAccountInfo> trustedAccountsUnderTheSameHolder;

    /**
     * These are accounts manually defined in by users in {@link gov.uk.ets.registry.api.tal.domain.TrustedAccount}.
     */
    private List<AcquiringAccountInfo> otherTrustedAccounts;

    /**
     * These are the accounts that are predefined in the system as candidate acquiring accounts for a specific
     * transaction type.
     */
    private List<AcquiringAccountInfo> predefinedCandidateAccounts;

    /**
     * This is the description of the predefined candidate accounts list, to be shown in the front-end.
     */
    private String predefinedCandidateAccountsDescription;

    public static class CandidateAcquiringAccountsBuilder {
        protected CandidateAcquiringAccountsBuilder isCandidateListPredefined(boolean isPredefined) {
            this.isCandidateListPredefined = isPredefined;
            return this;
        }

        /**
         * Builds the predefined candidate accounts list.
         * @param acquiringAccounts the predefined acquiring accounts.
         * @return the builder.
         */
        public CandidateAcquiringAccountsBuilder predefinedCandidateAccounts(
            List<AcquiringAccountInfo> acquiringAccounts) {
            this.predefinedCandidateAccounts = acquiringAccounts;
            this.isCandidateListPredefined = !acquiringAccounts.isEmpty();
            this.trustedAccountsUnderTheSameHolder =
                acquiringAccounts.isEmpty() ? this.trustedAccountsUnderTheSameHolder : new ArrayList<>();
            this.otherTrustedAccounts = acquiringAccounts.isEmpty() ? this.otherTrustedAccounts : new ArrayList<>();
            return this;
        }

        /**
         * Builds the trusted accounts under the same account holder list.
         * @param acquiringAccounts the trusted accounts under the same account holder.
         * @return the builder.
         */
        public CandidateAcquiringAccountsBuilder trustedAccountsUnderTheSameHolder(
            List<AcquiringAccountInfo> acquiringAccounts) {
            this.trustedAccountsUnderTheSameHolder =
                this.isCandidateListPredefined ? new ArrayList<>() : acquiringAccounts;
            return this;
        }

        /**
         * Builds the other trusted accounts list.
         * @param acquiringAccounts the other trusted accounts.
         * @return the builder.
         */
        public CandidateAcquiringAccountsBuilder otherTrustedAccounts(List<AcquiringAccountInfo> acquiringAccounts) {
            this.otherTrustedAccounts = this.isCandidateListPredefined ? new ArrayList<>() : acquiringAccounts;
            return this;
        }
    }
}
