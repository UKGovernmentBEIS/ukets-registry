package gov.uk.ets.registry.api.itl.notice.web.model;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationBlock;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ITLNoticeSearchDetailsResultMapper {
    // FIXME: Add lulufcActivity when the serialize issue gets fixed
    public List<ITLNoticeDetailResult> map(long notificationIdentifier, Set<ITLNotificationHistory> noticeLogHistories, Set<ITLNotificationBlock> noticeUnitBlocks) {
        List<ITLNoticeDetailResult> list = new ArrayList<>();
        Optional<Set<ITLNotificationHistory>> optional =  Optional.ofNullable(noticeLogHistories);
        List<ITLUnitIdentifierSearchResult> itlUnitIdentifierSearchResults = toSet(noticeUnitBlocks);
                if (optional.isPresent()) {
                    for (ITLNotificationHistory noticeLogHistory : optional.get()) {
                        list.add(ITLNoticeDetailResult.builder()
                                .id(noticeLogHistory.getId())
                                .commitPeriod(noticeLogHistory.getCommitmentPeriod() != null ?
                                        noticeLogHistory.getCommitmentPeriod().getCode() : null)
                                .createdDate(noticeLogHistory.getCreatedDate())
                                .messageDate(noticeLogHistory.getMessageDate())
                                .projectNumber(noticeLogHistory.getProjectNUmber())
                                .type(noticeLogHistory.getType())
                                .status(noticeLogHistory.getStatus())
                                .targetDate(noticeLogHistory.getTargetDate())
                                .content(noticeLogHistory.getContent())
                                .notificationIdentifier(notificationIdentifier)
                                .actionDueDate(noticeLogHistory.getActionDueDate())
                                .targetValue(noticeLogHistory.getTargetValue())
                                .unitType(noticeLogHistory.getUnitType() != null ?
                                        noticeLogHistory.getUnitType().getPrimaryCode() : null)
                                .unitBlockIdentifiers(itlUnitIdentifierSearchResults)
                                .build());
                    }
                }
        return list;
    }

    private List<ITLUnitIdentifierSearchResult> toSet(Set<ITLNotificationBlock> noticeUnitBlocks) {
        if (noticeUnitBlocks != null && !noticeUnitBlocks.isEmpty()){
            List<ITLUnitIdentifierSearchResult> itlUnitIdentifierSearchResults = new ArrayList<>();
            for (ITLNotificationBlock noticeUnitBlock : noticeUnitBlocks) {
                itlUnitIdentifierSearchResults.add(ITLUnitIdentifierSearchResult.builder()
                        .id(noticeUnitBlock.getId())
                        .originatingRegistryCode(noticeUnitBlock.getOriginatingRegistryCode())
                        .unitSerialBlockStart(noticeUnitBlock.getUnitSerialBlockStart())
                        .unitSerialBlockEnd(noticeUnitBlock.getUnitSerialBlockEnd())
                        .build());
            }
            return itlUnitIdentifierSearchResults;
        }
        return null;
    }
}
