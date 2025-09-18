package gov.uk.ets.registry.api.authz.ruleengine;

import java.util.EnumMap;
import java.util.Map;
import jakarta.validation.constraints.NotNull;

/**
 * Simple class containing the parameter values that will be used from the business rules down the road.
 */
public class RuleInputStore {
    Map<RuleInputType, Object> ruleInputs = new EnumMap<>(RuleInputType.class);

    /**
     * Saves the value of an input parameter.
     *
     * @param inputType  The type of the parameter
     * @param inputValue The value of of the parameter
     */
    public void put(@NotNull RuleInputType inputType, Object inputValue) {
        ruleInputs.put(inputType, inputValue);
    }

    /**
     * Gets the value of the parameter.
     *
     * @param inputType The input type
     * @return The value of the parameter
     */
    public Object get(@NotNull RuleInputType inputType) {
        return ruleInputs.get(inputType);
    }

    /**
     * Gets the value of the parameter.
     *
     * @param inputType The input type.
     * @param expectedClass The expected class.
     * @param <T> The parameterized type.
     * @return The value of the parameter.
     */
    public <T> T get(@NotNull RuleInputType inputType, Class<T> expectedClass) {
        return (T)ruleInputs.get(inputType);
    }

    /**
     * Checks if the input type parameter is already set.
     *
     * @param inputType The input type to check
     * @return true if the input parameter was set, false otherwise
     */
    public boolean containsKey(@NotNull RuleInputType inputType) {
        return ruleInputs.containsKey(inputType);
    }
}
