package gov.uk.ets.registry.api.file.upload.adhoc.services.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdHocEmailRecipientsError {

    INVALID_RECIPIENT_NOT_IN_FIRST_COLUMN("The first column should be the recipients' email"),
    INVALID_RECIPIENT("Invalid Recipient"),
    MISSING_HEADER("Missing header"),
    INVALID_HEADER_SPACES_NOT_ALLOWED("Invalid header. Spaces are not allowed"),
    DUPLICATE_COLUMNS("Duplicate columns"),
    DUPLICATE_RECIPIENTS("Duplicate recipients");
    
    private final String message;
}
