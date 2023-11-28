package gov.uk.ets.registry.api.file.reference.domain.type;

import gov.uk.ets.registry.api.task.domain.types.RequestType;
import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;

/**
 * Enumerates the various Reference Document files.
 * 
 * @author P35036
 */
@Getter
public enum ReferenceDocument {
    TEMPLATE_1_LETTER_OF_AUTHORITY("Template 1 - Letter of Authority.pdf"),
    TEMPLATE_2_PRIMARY_CONTACT_APPOINTMENT_AND_DECLARATION(
            "Template 2 - Primary Contact Appointment And Declaration.pdf"),
    TEMPLATE_3_AUTHORISED_REPRESENTATIVE_DECLARATION(
            "Template 3 - Authorised Representative Declaration.pdf"),
    TEMPLATE_4_ALTERNATIVE_PRIMARY_CONTACT_APPOINTMENT_AND_DECLARATION(
            "Template 4 - Alternative Primary Contact Appointment and Declaration.pdf"),
    TEMPLATE_5_COMBINED_PC_AND_LOA(
            "Template 5 - Combined Primary Contact and Letter of Authority Declaration.pdf");
    
    private String documentName;
    
    ReferenceDocument(String documentName) {
        this.documentName = documentName;
    }
    
    @Override
    public String toString() {
        return documentName;
    }

    public Set<RequestType> appliesForRequestTypes() {
        return EnumSet.of(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
    }

}
