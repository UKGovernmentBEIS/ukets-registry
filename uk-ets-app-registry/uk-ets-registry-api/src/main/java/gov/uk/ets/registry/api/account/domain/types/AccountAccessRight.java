package gov.uk.ets.registry.api.account.domain.types;

public enum AccountAccessRight {

    ROLE_BASED,

    INITIATE_AND_APPROVE,

    APPROVE,

    INITIATE,

    SURRENDER_INITIATE_AND_APPROVE,

    READ_ONLY;

    public boolean containsRight(AccountAccessRight accessRight) {
        switch (this) {
            case INITIATE_AND_APPROVE: {
                return true;
            }
            case INITIATE: {
                switch (accessRight) {
                    case INITIATE_AND_APPROVE:
                    case APPROVE:
                    case SURRENDER_INITIATE_AND_APPROVE: {
                        return false;
                    }
                    default:
                        return true;
                }
            }
            case APPROVE: {
                switch (accessRight) {
                    case INITIATE_AND_APPROVE:
                    case INITIATE:
                    case SURRENDER_INITIATE_AND_APPROVE: {
                        return false;
                    }
                    default:
                        return true;
                }
            }
            case SURRENDER_INITIATE_AND_APPROVE: {
                switch (accessRight) {
                    case INITIATE_AND_APPROVE:
                    case INITIATE:
                    case APPROVE: {
                        return false;
                    }
                    default:
                        return true;
                }
            }
            case READ_ONLY: {
                return accessRight == AccountAccessRight.READ_ONLY;
            }
            case ROLE_BASED: {
                return accessRight == AccountAccessRight.ROLE_BASED;
            }
        }
        return false;
    }

    /**
     * Parses the input string as an enum value, if exists.
     * @param input the input string.
     * @return the corresponding enum value, null if parse fails.
     */
    public static AccountAccessRight parse (String input) {
        AccountAccessRight accessRight;
        try {
            accessRight = AccountAccessRight.valueOf(input.replaceAll("[\\s-]","_").toUpperCase());
        } catch (IllegalArgumentException exception) {
            accessRight = null;
        }
        return accessRight;
    }
}
