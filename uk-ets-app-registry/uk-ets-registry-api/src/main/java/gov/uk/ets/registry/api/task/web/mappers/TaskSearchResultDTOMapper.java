package gov.uk.ets.registry.api.task.web.mappers;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.task.shared.TaskProjection;
import gov.uk.ets.registry.api.task.web.model.TaskSearchResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Maps a Task Projection search result to the corresponded web data transfer
 * object according to user role (if user is admin or not)
 **/
@Component
@RequiredArgsConstructor
public class TaskSearchResultDTOMapper {

    private final AccountAccessService accountAccessService;

    /**
     * @param projection The {@link TaskProjection} projection
     * @return The {@link TaskSearchResult} web data transfer object
     */
    public TaskSearchResult get(TaskProjection projection, List<AccountAccess> accesses) {
        TaskSearchResult dto = new TaskSearchResult();
        BeanUtils.copyProperties(projection, dto);
        dto.setCreatedOn(projection.getCreatedOn());
        dto.setUserHasAccess(hasAccess(projection, accesses));
        return dto;
    }

    private boolean hasAccess(TaskProjection projection, List<AccountAccess> accesses) {
        return accesses.stream()
            .anyMatch(
                accountAccess -> accountAccess.getAccount().getIdentifier()
                    .equals(NumberUtils.toLong(projection.getAccountNumber()))
            );
    }
}
