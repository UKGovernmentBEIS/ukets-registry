package gov.uk.ets.registry.api.itl.message.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = {"messageId"})
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITLMessageSendResponse {

	private boolean success;
	private Long messageId;
}
