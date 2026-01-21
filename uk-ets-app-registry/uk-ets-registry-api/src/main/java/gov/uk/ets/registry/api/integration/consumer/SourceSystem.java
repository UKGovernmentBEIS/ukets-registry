package gov.uk.ets.registry.api.integration.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SourceSystem {
    REGISTRY("REGISTRY", "Registry"),
    METSIA_INSTALLATION("METSIA", "METS-Installation"),
    METSIA_AVIATION("METSIA", "METS-Aviation"),
    MARITIME("MARITIME", "METS-Maritime");

    private final String source;
    private final String label;
}
