package gov.uk.ets.compliance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.uk.ets.compliance.domain.events.EventHistory;
import gov.uk.ets.compliance.domain.events.base.DynamicComplianceEvent;
import gov.uk.ets.compliance.domain.events.base.StaticComplianceRequestEvent;
import gov.uk.ets.compliance.service.DynamicComplianceException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
public class DynamicCompliance {

    private Long compliantEntityId;
    private ComplianceState state;

    @JsonIgnore
    private final DynamicComplianceCalculator calculator = new DynamicComplianceCalculator();
    @JsonIgnore
    private final LastYearDynamicComplianceCalculator lastYearStatusCalculator = new LastYearDynamicComplianceCalculator();
    private final StateHistory stateHistory = new StateHistory();
    private final EventHistory eventHistory = new EventHistory();

    /**
     * Creates a new dynamic compliance and invokes the calculator to calculate its state.
     *
     * @param compliantEntityId            the compliant entity id
     * @param currentYear                  the current year
     * @param firstYearOfVerifiedEmissions the first year of verified emissions
     * @param lastYearOfVerifiedEmissions  the last year of verified emissions
     */
    public DynamicCompliance(Long compliantEntityId, int currentYear, Integer firstYearOfVerifiedEmissions,
                             Integer lastYearOfVerifiedEmissions, LocalDateTime dateRequested) {

        this.compliantEntityId = compliantEntityId;
        this.state = ComplianceState
                .initialize(currentYear, firstYearOfVerifiedEmissions, lastYearOfVerifiedEmissions);
        this.state.status(calculator.calculate(this.state));
        if(this.state.isInLastYearOfVerifiedEmissions()) {
            this.state.lastYearStatus(lastYearStatusCalculator.calculate(this.state));
        }
        // similarly to the status, the request date must be also set for the initial state,
        // as well as for all subsequent states
        this.state.saveRequestDate(dateRequested);
    }

    /**
     * Copies the current compliance states, calculates and validates the next one.
     * stores the event and previous event in the event and state history respectively
     *
     * @param event the incoming event.
     */
    public boolean processDynamicComplianceUpdateEvent(DynamicComplianceEvent event) {
        // always calculate on a deep copy and never on the current state object
        ComplianceState newState = event.updateComplianceState(state.deepCopy());
        if (newState.isValid()) {
            newState.status(calculator.calculate(newState));
            if(newState.isInLastYearOfVerifiedEmissions()) {
                newState.lastYearStatus(lastYearStatusCalculator.calculate(newState));
            } else {
                newState.lastYearStatus(null);
            }
            // dateRequest is a common field in all events, so we want to store it for every event type.
            newState.saveRequestDate(event.getDateRequested());
            stateHistory.archive(this.state);
            // only events that result in a valid state are marked as processed
            event.markAsProcessed();
            state = newState;
        }

        eventHistory.save(event);
        return newState.isValid();
    }

    /**
     * Retrieves the {@link ComplianceState} as calculated at the requested date.
     */
    public ComplianceState processStaticComplianceRequestEvent(StaticComplianceRequestEvent event) {
        LocalDateTime dateRequested = event.getDateRequested();
        // create stream with all state history plus current state
        // just in case there are old events without dateRequested
        // keep states calculated before the date requested
        ComplianceState complianceState = Stream.concat(this.stateHistory.getHistory().stream(), Stream.of(this.state))
                .filter(Objects::nonNull)
                // just in case there are old events without dateRequested
                .filter(state -> state.getDateRequested() != null)
                // keep states calculated before the date requested
                .filter(state -> state.getDateRequested().isBefore(dateRequested))
                .max(Comparator.comparing(ComplianceState::getDateRequested))
                .orElseThrow(() -> new DynamicComplianceException(String
                        .format("Dynamic Compliance State not found for compliant entity id: %s and date requested: %s",
                                event.getCompliantEntityId(), event.getDateRequested()))
                );
        eventHistory.save(event);
        return complianceState;
    }
}
