package gov.uk.ets.registry.api.helper.persistence;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.EntityManager;

import org.assertj.core.util.Arrays;

import lombok.Builder;
import lombok.Getter;

public class TaskModelTestHelper {
  private EntityManager entityManager;

  public TaskModelTestHelper(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Builder
  @Getter
  public static class AddUserCommand {
    private String iamIdentifier;
    private String firstName;
    private String lastName;
    private String disclosedName;
    private String urid;
  }

  @Builder
  @Getter
  public static class AddTaskCommand {
    private Long requestId;
    private RequestType requestType;
    private RequestStateEnum status;
    private User initiator;
    private User approver;
    private User claimant;
    private Account account;
    private String transactionIdentifier;
    private Date initiatedDate;
    private Date claimedDate;
    private Date completedDate;
    private String difference;
  }

  public User addUser(AddUserCommand addUserCommand) {
    User user = new User();

    user.setIamIdentifier(addUserCommand.iamIdentifier);
    user.setFirstName(addUserCommand.firstName);
    user.setLastName(addUserCommand.lastName);
    user.setDisclosedName(addUserCommand.disclosedName);
    user.setUrid(addUserCommand.urid);

    entityManager.persist(user);

    return user;
  }

  public Task addTask(AddTaskCommand addTaskCommand) {
      
    TaskTransaction taskTransaction = new TaskTransaction();
    taskTransaction.setTransactionIdentifier(addTaskCommand.transactionIdentifier);
      
    Task task = new Task();
    task.setRequestId(addTaskCommand.requestId);
    task.setType(addTaskCommand.requestType);
    task.setStatus(addTaskCommand.status);
    task.setInitiatedBy(addTaskCommand.initiator);
    task.setClaimedBy(addTaskCommand.claimant);
    task.setAccount(addTaskCommand.account);
    task.setTransactionIdentifiers(Objects.nonNull(addTaskCommand.transactionIdentifier) ? List.of(taskTransaction) : null);
    task.setInitiatedDate(addTaskCommand.initiatedDate);
    task.setClaimedDate(addTaskCommand.claimedDate);
    task.setCompletedBy(addTaskCommand.approver);
    task.setCompletedDate(addTaskCommand.completedDate);
    task.setDifference(addTaskCommand.difference);
    
    taskTransaction.setTask(task);
    entityManager.persist(task);
    entityManager.persist(taskTransaction);

    return task;
  }
}
