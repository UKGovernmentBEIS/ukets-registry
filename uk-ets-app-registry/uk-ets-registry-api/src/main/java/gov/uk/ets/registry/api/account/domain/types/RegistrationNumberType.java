package gov.uk.ets.registry.api.account.domain.types;

import lombok.Getter;

@Getter
public enum RegistrationNumberType {
    REGISTRATION_NUMBER(0),
    REGISTRATION_NUMBER_REASON(1);

    private int type;

    RegistrationNumberType(int type) {
        this.type = type;
    }
}
