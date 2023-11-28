package gov.uk.ets.registry.api.itl.notice.domain.type;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * the status of the {@link ITLNotification}
 */
@AllArgsConstructor
@Getter
public enum NoticeStatus {
    OPEN(1),
    INCOMPLETE(2),
    COMPLETED(3);

    private Integer code;

    /**
     * Identifies the notice status based on the notice status code.
     * @param noticeStatusCode The notice status code.
     * @return a notice status.
     */
    public static NoticeStatus of(int noticeStatusCode) {
        NoticeStatus noticeStatus = null;
        Optional<NoticeStatus> optional = Stream.of(values())
                .filter(type ->
                        type.getCode().equals(noticeStatusCode))
                .findFirst();
        if (optional.isPresent()) {
            noticeStatus = optional.get();
        }
        return noticeStatus;
    }
}
