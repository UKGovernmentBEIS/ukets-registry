package gov.uk.ets.registry.api.account.web.model.search;

import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountTypeOption {
    public static AccountTypeOption of(AccountType type) {
        return AccountTypeOption.builder()
                .label(type.getLabel())
                .value(type.name()).build();
    }

    private final String label;
    private final String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountTypeOption that = (AccountTypeOption) o;
        return Objects.equals(label, that.label) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, value);
    }
}
