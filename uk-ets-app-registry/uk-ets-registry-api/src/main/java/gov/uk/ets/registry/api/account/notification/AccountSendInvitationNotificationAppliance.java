package gov.uk.ets.registry.api.account.notification;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.notification.AccountSendInvitationGroupNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
 * <ul>
 *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
 *     <li>One of the following {@link GroupNotificationType} were
 *     used: {@link GroupNotificationType#SEND_INVITATION_TO_CONTACTS}</li>
 * </ul>
 */
@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class AccountSendInvitationNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;

    @AfterReturning(
            value = "@annotation(emitsGroupNotificationsAnnotation)",
            returning = "result"
    )
    public void apply(JoinPoint joinPoint,
                      EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      Object result) {

        if (Arrays.stream(emitsGroupNotificationsAnnotation.value())
                .noneMatch(type -> type == GroupNotificationType.SEND_INVITATION_TO_CONTACTS)) {
            return;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        AccountContactSendInvitationDTO payload = null;
        AccountDTO accountDTO = null;

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {

                if (annotation instanceof InvitationPayload && args[i] instanceof AccountContactSendInvitationDTO) {
                    payload = (AccountContactSendInvitationDTO) args[i];
                }

                if (annotation instanceof InvitationAccount && args[i] instanceof AccountDTO) {
                    accountDTO = (AccountDTO) args[i];
                }
            }
        }

        require(payload != null, "Missing @InvitationPayload parameter");
        require(accountDTO != null, "Missing @InvitationAccount parameter");


        for (GroupNotificationType type : emitsGroupNotificationsAnnotation.value()) {

            if (type == GroupNotificationType.SEND_INVITATION_TO_CONTACTS) {

                final AccountDTO finalAccountDTO = accountDTO;
                final String claimCode = finalAccountDTO.getAccountClaimCode();


                payload.getMetsContacts().forEach(contact ->
                        emitAfterCommit(() ->
                                groupNotificationClient.emitGroupNotification(
                                        buildNotification(type, finalAccountDTO, contact, Boolean.TRUE, claimCode)
                                )
                        )
                );

                payload.getRegistryContacts().forEach(contact ->
                        emitAfterCommit(() ->
                                groupNotificationClient.emitGroupNotification(
                                        buildNotification(type, finalAccountDTO, contact, Boolean.FALSE, claimCode)
                                )
                        )
                );
            }
        }
    }

    private GroupNotification buildNotification(
            GroupNotificationType type,
            AccountDTO account,
            AccountContactDTO contact,
            boolean isMetsContact,
            String accountClaimCode
    ) {
        return AccountSendInvitationGroupNotification.builder()
                .recipients(Set.of(contact.getEmail()))
                .contactFullName(contact.getFullName())
                .operatorName(account.getAccountHolder().actualName())
                .accountType(account.getAccountDetails().getAccountType())
                .accountNumber(account.getAccountDetails().getAccountNumber())
                .emitterId(account.getOperator().getEmitterId())
                .accountClaimCode(accountClaimCode)
                .isMetsContact(isMetsContact)
                .type(type)
                .build();
    }

    private void emitAfterCommit(Runnable action) {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        action.run();
                    }
                }
        );
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new MissingInvitationNotificationDataException(message);
        }
    }
}
