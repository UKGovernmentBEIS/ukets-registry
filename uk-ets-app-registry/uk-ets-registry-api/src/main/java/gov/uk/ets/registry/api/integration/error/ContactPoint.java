package gov.uk.ets.registry.api.integration.error;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContactPoint {

    SERVICE_DESK("Fordway"),
    TU_SUPPORT("TU SD/Support"),
    DESNZ("DESNZ"),
    REGISTRY_ADMINISTRATORS("Registry Administrators"),
    METS_REGULATORS("METS Regulators");

    private final String description;

    public static List<ContactPoint> registryAdministrators() {
        return List.of(REGISTRY_ADMINISTRATORS);
    }

    public static List<ContactPoint> metsRegulators() {
        return List.of(ContactPoint.METS_REGULATORS);
    }

    public static List<ContactPoint> desnz() {
        return List.of(DESNZ);
    }

    public static List<ContactPoint> registryDesnz() {
        return List.of(REGISTRY_ADMINISTRATORS, DESNZ);
    }

    public static List<ContactPoint> registryMets() {
        return List.of(REGISTRY_ADMINISTRATORS, METS_REGULATORS);
    }

    public static List<ContactPoint> registryServiceDeskAndTuSupport() {
        return List.of(REGISTRY_ADMINISTRATORS, SERVICE_DESK, TU_SUPPORT);
    }

    public static List<ContactPoint> serviceDeskAndTU() {
        return List.of(SERVICE_DESK, TU_SUPPORT);
    }

}
