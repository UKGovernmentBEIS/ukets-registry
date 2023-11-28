package gov.uk.ets.publication.api.error;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
@ToString
public class ErrorBody {

    private List<ErrorDetail> errorDetails;

    public static ErrorBody from(String message) {
        return ErrorBody.builder()
            .errorDetails(Collections.singletonList(ErrorDetail.builder().message(message).build())).build();
    }
}
