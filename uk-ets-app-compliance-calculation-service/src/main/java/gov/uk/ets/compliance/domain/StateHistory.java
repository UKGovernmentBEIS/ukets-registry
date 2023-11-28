package gov.uk.ets.compliance.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StateHistory {
    private List<ComplianceState> history = new ArrayList<>();

    public void archive(ComplianceState state) {
        history.add(state);
    }

    public Stream<ComplianceState> snapshots() {
        return history.stream();
    }
}
