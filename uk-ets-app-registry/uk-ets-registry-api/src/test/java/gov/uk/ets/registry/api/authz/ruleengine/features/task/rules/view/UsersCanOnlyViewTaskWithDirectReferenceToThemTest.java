package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;

class UsersCanOnlyViewTaskWithDirectReferenceToThemTest {
	private BusinessSecurityStore securityStore;

	@Mock
	private Account account;

	@Mock
	private User initiator;

	@Mock
	private List<UserRole> userRoles;
	
	final RequestType anyRequestType = RequestType.ADD_TRUSTED_ACCOUNT_REQUEST;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		securityStore = new BusinessSecurityStore();
		securityStore.setAccount(account);
		securityStore.setUserRoles(userRoles);
	}

	@Test
	void error() {
		ErrorBody errorBody = new ARsCanViewSpecificTaskTypes(securityStore).error();
		assertNotNull(errorBody);
		assertEquals(1, errorBody.getErrorDetails().size());
	}

	@Test
	void test_ruleNotApplicable_ifDirectReferenceToUserNotExists() {
		User loggedInUser = getSecondAccountAR();
		securityStore.setUser(loggedInUser);

		TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
		TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
		ruleInfo.setTask(getTask(anyRequestType, null));
		slice.setTaskBusinessRuleInfoList(List.of(ruleInfo));
		securityStore.setTaskBusinessSecurityStoreSlice(slice);

		UsersCanOnlyViewTaskWithDirectReferenceToThem rule = 
				new UsersCanOnlyViewTaskWithDirectReferenceToThem(securityStore);
		assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
	}

	@Test
	void test_ruleNotApplicable_ifTaskIsRequestDocForAccHolderAndDirectReferenceToUserExists() {
		User loggedInUser = getSecondAccountAR();
		securityStore.setUser(loggedInUser);

		TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
		TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
		ruleInfo.setTask(getTask(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD, getAccountAR()));
		slice.setTaskBusinessRuleInfoList(List.of(ruleInfo));
		securityStore.setTaskBusinessSecurityStoreSlice(slice);

		UsersCanOnlyViewTaskWithDirectReferenceToThem rule = 
				new UsersCanOnlyViewTaskWithDirectReferenceToThem(securityStore);
		assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
	}

	@Test
	void test_userCanAccessTask_ifDirectReferenceToHimExists() {
		User loggedInUser = getSecondAccountAR();
		securityStore.setUser(loggedInUser);

		TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
		TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
		ruleInfo.setTask(getTask(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST, getSecondAccountAR()));
		slice.setTaskBusinessRuleInfoList(List.of(ruleInfo));
		securityStore.setTaskBusinessSecurityStoreSlice(slice);

		UsersCanOnlyViewTaskWithDirectReferenceToThem rule = 
				new UsersCanOnlyViewTaskWithDirectReferenceToThem(securityStore);
		assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
	}

	@Test
	void test_userCannotAccessTask_ifDirectReferenceToOtherUserExists() {
		User loggedInUser = getSecondAccountAR();
		securityStore.setUser(loggedInUser);

		TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
		TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
		ruleInfo.setTask(getTask(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST, getAccountAR()));
		slice.setTaskBusinessRuleInfoList(List.of(ruleInfo));
		securityStore.setTaskBusinessSecurityStoreSlice(slice);

		UsersCanOnlyViewTaskWithDirectReferenceToThem rule = 
				new UsersCanOnlyViewTaskWithDirectReferenceToThem(securityStore);
		assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
	}

	private Task getTask(RequestType taskType, User assignTo) {
		Task task = new Task();
		task.setType(taskType);
		task.setAccount(account);
		task.setInitiatedBy(initiator);
		task.setUser(assignTo);
		return task;
	}

	private User getAccountAR() {
		User representative = new User();
		representative.setId(1L);
		return representative;
	}

	private User getSecondAccountAR() {
		User representative = new User();
		representative.setId(2L);
		return representative;
	}
}
