package uk.gov.ets.kp.webservices.registry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServletEndpointContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import uk.gov.ets.kp.webservices.registry.RegistryBindingImplTest.TestCase.Builder;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeRequest;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeResponse;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationRequest;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationResponse;
import uk.gov.ets.kp.webservices.shared.types.MessageRequest;
import uk.gov.ets.kp.webservices.shared.types.MessageResponse;
import uk.gov.ets.kp.webservices.shared.types.NotificationRequest;
import uk.gov.ets.kp.webservices.shared.types.NotificationResponse;
import uk.gov.ets.kp.webservices.shared.types.ProposalRequest;
import uk.gov.ets.kp.webservices.shared.types.ProposalResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse;

@ExtendWith(MockitoExtension.class)
class RegistryBindingImplTest {

    RegistryBindingImpl registryEndpoint = new RegistryBindingImpl();

    @Mock
    RegistryService registryService;

    @Mock
    ServletEndpointContext servletEndpointContext;

    @Mock
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void init() {
        when(webApplicationContext.getBean(RegistryService.class)).thenReturn(registryService);
        try (MockedStatic<WebApplicationContextUtils> mock = Mockito.mockStatic(WebApplicationContextUtils.class)) {
            mock.when(() -> WebApplicationContextUtils.getRequiredWebApplicationContext(any())).thenReturn(webApplicationContext);
            registryEndpoint.init(servletEndpointContext);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("initiateReconciliation should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void initiateReconciliation() throws RemoteException {
        verifyDelegation(new Builder<InitiateReconciliationRequest, InitiateReconciliationResponse>()
            .requestType(InitiateReconciliationRequest.class)
            .responseType(InitiateReconciliationResponse.class)
            .execution(request -> registryEndpoint.initiateReconciliation(request))
            .delegation(request -> registryService.initiateReconciliation(request))
            .verification(request -> then(registryService).should().initiateReconciliation(request))
            .build());
    }

    @DisplayName("provideTime should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void provideTime() throws RemoteException {
        verifyDelegation(new Builder<ProvideTimeRequest, ProvideTimeResponse>()
            .requestType(ProvideTimeRequest.class)
            .responseType(ProvideTimeResponse.class)
            .execution(request -> registryEndpoint.provideTime(request))
            .delegation(request -> registryService.provideTime(request))
            .verification(request -> then(registryService).should().provideTime(request))
            .build());
    }

    @DisplayName("acceptMessage should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void acceptMessage() throws RemoteException {
        verifyDelegation(new Builder<MessageRequest, MessageResponse>()
            .requestType(MessageRequest.class)
            .responseType(MessageResponse.class)
            .execution(request -> registryEndpoint.acceptMessage(request))
            .delegation(request -> registryService.acceptMessage(request))
            .verification(request -> then(registryService).should().acceptMessage(request))
            .build());
    }

    @DisplayName("acceptProposal should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void acceptProposal() throws RemoteException {
        verifyDelegation(new Builder<ProposalRequest, ProposalResponse>()
            .requestType(ProposalRequest.class)
            .responseType(ProposalResponse.class)
            .execution(request -> registryEndpoint.acceptProposal(request))
            .delegation(request -> registryService.acceptProposal(request))
            .verification(request -> then(registryService).should().acceptProposal(request))
            .build());
    }

    @DisplayName("acceptNotification should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void acceptNotification() throws RemoteException {
        verifyDelegation(new Builder<NotificationRequest, NotificationResponse>()
            .requestType(NotificationRequest.class)
            .responseType(NotificationResponse.class)
            .execution(request -> registryEndpoint.acceptNotification(request))
            .delegation(request -> registryService.acceptNotification(request))
            .verification(request -> then(registryService).should().acceptNotification(request))
            .build());
    }

    @DisplayName("acceptITLNotice should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void acceptITLNotice() throws RemoteException {
        verifyDelegation(new Builder<ITLNoticeRequest, ITLNoticeResponse>()
            .requestType(ITLNoticeRequest.class)
            .responseType(ITLNoticeResponse.class)
            .execution(request -> registryEndpoint.acceptITLNotice(request))
            .delegation(request -> registryService.acceptITLNotice(request))
            .verification(request -> then(registryService).should().acceptITLNotice(request))
            .build());
    }

    @DisplayName("receiveReconciliationResult should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void receiveReconciliationResult() throws RemoteException {
        verifyDelegation(new Builder<ReconciliationResultRequest, ReconciliationResultResponse>()
            .requestType(ReconciliationResultRequest.class)
            .responseType(ReconciliationResultResponse.class)
            .execution(request -> registryEndpoint.receiveReconciliationResult(request))
            .delegation(request -> registryService.receiveReconciliationResult(request))
            .verification(request -> then(registryService).should().receiveReconciliationResult(request))
            .build());
    }

    @DisplayName("provideAuditTrail should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void provideAuditTrail() throws RemoteException {
        verifyDelegation(new Builder<ProvideAuditTrailRequest, ProvideAuditTrailResponse>()
            .requestType(ProvideAuditTrailRequest.class)
            .responseType(ProvideAuditTrailResponse.class)
            .execution(request -> registryEndpoint.provideAuditTrail(request))
            .delegation(request -> registryService.provideAuditTrail(request))
            .verification(request -> then(registryService).should().provideAuditTrail(request))
            .build());
    }

    @DisplayName("provideTotals should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void provideTotals() throws RemoteException {
        verifyDelegation(new Builder<ProvideTotalsRequest, ProvideTotalsResponse>()
            .requestType(ProvideTotalsRequest.class)
            .responseType(ProvideTotalsResponse.class)
            .execution(request -> registryEndpoint.provideTotals(request))
            .delegation(request -> registryService.provideTotals(request))
            .verification(request -> then(registryService).should().provideTotals(request))
            .build());
    }

    @DisplayName("provideUnitBlocks should delegate the request handling to the RegistryService Spring bean and return the response of the bean method.")
    @Test
    void provideUnitBlocks() throws RemoteException {
        verifyDelegation(new Builder<ProvideUnitBlocksRequest, ProvideUnitBlocksResponse>()
            .requestType(ProvideUnitBlocksRequest.class)
            .responseType(ProvideUnitBlocksResponse.class)
            .execution(request -> registryEndpoint.provideUnitBlocks(request))
            .delegation(request -> registryService.provideUnitBlocks(request))
            .verification(request -> then(registryService).should().provideUnitBlocks(request))
            .build());
    }

    <T, K> void verifyDelegation(TestCase<T, K> testCase) throws RemoteException{
        // given
        K expectedResponse = mock(testCase.responseType);
        T request = mock(testCase.requestType);
        given(testCase.delegation.apply(request)).willReturn(expectedResponse);

        //when
        K response = testCase.execution.apply(request);

        // then
        testCase.verification.accept(request);
        assertEquals(expectedResponse, response);
    }

    static class TestCase<T, K> {
        private Class<T> requestType;
        private Class<K> responseType;
        private Consumer<T> verification;
        private Function<T, K> delegation;
        private ThrowableFunction<T, K> execution;

        private TestCase(Class<T> requestType, Class<K> responseType, Consumer<T> verification,
            Function<T, K> delegation, ThrowableFunction<T, K> execution) {
            this.requestType = requestType;
            this.responseType = responseType;
            this.verification = verification;
            this.delegation = delegation;
            this.execution = execution;
        }

        public static class Builder<T, K> {
            private Class<T> requestType;
            private Class<K> responseType;
            private Consumer<T> verification;
            private Function<T, K> delegation;
            private ThrowableFunction<T, K> execution;

            public Builder<T, K> requestType(Class<T> requestType) {
                this.requestType = requestType;
                return this;
            }

            public Builder<T, K> responseType(Class<K> responseType) {
                this.responseType = responseType;
                return this;
            }

            public Builder<T, K> verification(Consumer<T> delegation) {
                this.verification = delegation;
                return this;
            }

            public Builder<T, K> delegation(Function<T, K> mock) {
                this.delegation = mock;
                return this;
            }

            public Builder<T, K> execution(ThrowableFunction<T, K> execution) {
                this.execution = execution;
                return this;
            }

            public TestCase<T, K> build() {
                return new TestCase<>(requestType, responseType, verification, delegation, execution);
            }
        }
    }

    @FunctionalInterface
    public interface ThrowableFunction<T, R> {
        R apply(T t) throws RemoteException;
    }
}