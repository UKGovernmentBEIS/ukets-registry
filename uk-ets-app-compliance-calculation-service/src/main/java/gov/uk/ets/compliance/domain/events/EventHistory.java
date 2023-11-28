package gov.uk.ets.compliance.domain.events;

import gov.uk.ets.compliance.domain.events.base.ComplianceEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter
public class EventHistory {

    List<ComplianceEvent> archive = new ArrayList<>();

    public void save(ComplianceEvent event) {
        archive.add(event);
    }

    Stream<ComplianceEvent> events() {
        return archive.stream();
    }
}
