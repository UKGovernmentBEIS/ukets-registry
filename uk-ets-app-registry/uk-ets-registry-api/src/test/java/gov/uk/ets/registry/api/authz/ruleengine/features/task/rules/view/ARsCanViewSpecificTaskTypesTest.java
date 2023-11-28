package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
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

class ARsCanViewSpecificTaskTypesTest {
	private BusinessSecurityStore securityStore;

	@Mock
	private User user;

	@Mock
	private Account account;

	@Mock
	private List<UserRole> userRoles;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		securityStore = new BusinessSecurityStore();
		securityStore.setAccount(account);
		securityStore.setUser(user);
		securityStore.setUserRoles(userRoles);
	}

	@Test
	void error() {
		ErrorBody errorBody = new ARsCanViewSpecificTaskTypes(securityStore).error();
		assertNotNull(errorBody);
		assertEquals(1, errorBody.getErrorDetails().size());
	}

	@Test
	void test_arsCannotViewChangeTokenTask() {
		List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
		TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
		ruleInfo.setTask(getTask(RequestType.CHANGE_TOKEN));
		taskBusinessRuleInfoList.add(ruleInfo);

		securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));

		TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
		slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
		securityStore.setTaskBusinessSecurityStoreSlice(slice);
		ARsCanViewSpecificTaskTypes rule = new ARsCanViewSpecificTaskTypes(securityStore);
		assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
	}

	@Test
	void test_arsCannotViewLostTokenTask() {
		List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
		TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
		ruleInfo.setTask(getTask(RequestType.LOST_TOKEN));
		taskBusinessRuleInfoList.add(ruleInfo);

		securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));

		TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
		slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
		securityStore.setTaskBusinessSecurityStoreSlice(slice);
		ARsCanViewSpecificTaskTypes rule = new ARsCanViewSpecificTaskTypes(securityStore);
		assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
	}

	@Test
	void test_arsCannotViewLostPasswordAndTokenTask() {
		List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
		TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
		ruleInfo.setTask(getTask(RequestType.LOST_PASSWORD_AND_TOKEN));
		taskBusinessRuleInfoList.add(ruleInfo);

		securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));

		TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
		slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
		securityStore.setTaskBusinessSecurityStoreSlice(slice);
		ARsCanViewSpecificTaskTypes rule = new ARsCanViewSpecificTaskTypes(securityStore);
		assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
	}

	private Task getTask(RequestType taskType) {
		Task task = new Task();
		task.setType(taskType);
		task.setAccount(account);
		return task;
	}
}
