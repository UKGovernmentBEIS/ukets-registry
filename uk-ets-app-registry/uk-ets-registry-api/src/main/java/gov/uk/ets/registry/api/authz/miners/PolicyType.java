package gov.uk.ets.registry.api.authz.miners;

import java.util.Arrays;

enum PolicyType {
    UNKNOWN(""),
    AGGREGATE("aggregate"),
    ROLE("role"),
    RESOURCE("resource"),
    SCOPE("scope"),
    ONLY_ENROLLED_POLICY("script-only-enrolled-policy.js"),
    ONLY_REGISTERED_POLICY("script-only-registered-policy.js"),
    ONLY_VALIDATED_POLICY("script-only-validated-policy.js");

    private final String rawName;

    PolicyType(String name) {
        this.rawName = name;
    }

    public static PolicyType of(String policyType) {
        return Arrays.stream(values()).filter(pred -> pred.lowName().equals(policyType)).findFirst().orElse(UNKNOWN);
    }

    public String lowName() {
        return rawName;
    }
}
