package gov.uk.ets.registry.api.account.web.model.search;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionTypeOption {
    public static TransactionTypeOption of(TransactionType type) {
        return TransactionTypeOption.builder()
            .label(type.getDescription())
            .value(type.name()).build();
    }

    private final String label;
    private final String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTypeOption that = (TransactionTypeOption) o;
        return Objects.equals(label, that.label) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, value);
    }
}
