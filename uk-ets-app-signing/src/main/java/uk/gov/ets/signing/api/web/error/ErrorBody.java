package uk.gov.ets.signing.api.web.error;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@Builder
@EqualsAndHashCode
public class ErrorBody {

    private List<ErrorDetail> errorDetails;

    public static ErrorBody from(String message) {
        return ErrorBody.builder()
            .errorDetails(Collections.singletonList(ErrorDetail.builder().message(message).build())).build();
    }
}
