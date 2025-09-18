package gov.uk.ets.registry.api.payment.web.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PaymentFileDTO {

    private Long id;
    private String fileName;
    private String fileSize;
}
