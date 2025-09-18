package gov.uk.ets.registry.api.user.profile.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * {@link BooleanExpression} factory for tasks of type of {@link RequestType#REQUESTED_EMAIL_CHANGE}
 */
@Component
@RequiredArgsConstructor
public class EmailChangeBooleanExpressionFactory {
    private final UserService userService;

    /**
     * Gets the {@link BooleanExpression} for getting the pending email change tasks of current user.
     * @return The {@link BooleanExpression}
     */
    public BooleanExpression getCurrentUserPendingEmailChangesExpression() {
        return getPendingEmailChangesExpression()
            .and(QTask.task.user.eq(userService.getCurrentUser()));
    }

    public BooleanExpression getOtherUserPendingEmailChangesExpression(String urid) {
        return getPendingEmailChangesExpression()
            .and(QTask.task.user.urid.eq(urid));
    }

    private BooleanExpression getPendingEmailChangesExpression() {
        return QTask.task.type.eq(RequestType.REQUESTED_EMAIL_CHANGE)
            .and(QTask.task.status.eq(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED));
    }
}
