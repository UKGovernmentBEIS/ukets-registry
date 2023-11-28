package gov.uk.ets.registry.api.common.error;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;


@Getter
@Builder
@Jacksonized // this annotates builder methods with jackson annotations.
// This way, no constructors are needed for deserialization.
@EqualsAndHashCode
@ToString
public class ErrorBody {

    private List<ErrorDetail> errorDetails;

    public static ErrorBody from(String message) {
        return ErrorBody.builder()
            .errorDetails(Collections.singletonList(ErrorDetail.builder().message(message).build())).build();
    }
}
