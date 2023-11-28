package gov.uk.ets.registry.api.ar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.ar.web.model.ReplaceARRequest;
import gov.uk.ets.registry.api.ar.web.model.UpdateARRightsRequest;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.uk.ets.registry.api.ar.web.model.UpdateARRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ARUpdateRequestsTest {

    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;

    private AuthorizedRepresentativeController controller;

    private Long accountId;
    private AccountAccessRight accountAccessRight;
    private String candidateUrid;
    private String replaceeUrid;
    private Long requestId;
    private ArgumentCaptor<ARUpdateActionDTO> arUpdateActionDTO;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // given
        controller = new AuthorizedRepresentativeController(authorizedRepresentativeService);
        accountId = 12323L;
        accountAccessRight = AccountAccessRight.INITIATE_AND_APPROVE;
        candidateUrid = "UK213213";
        replaceeUrid = "UK9865656";
        requestId = 123213L;

        given(authorizedRepresentativeService.placeUpdateRequest(any())).willReturn(requestId);
        arUpdateActionDTO = ArgumentCaptor.forClass(ARUpdateActionDTO.class);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addAuthorizedRepresentative() throws Exception {
        verifyUpdateARAccessRightsBehaviour("/api-registry/authorised-representatives.add", ARUpdateActionType.ADD);
    }

    @Test
    void removeAuthorizedRepresentative() throws Exception {
        verifyUpdateARStateBehavior("/api-registry/authorised-representatives.remove", ARUpdateActionType.REMOVE);
    }

    @Test
    void suspendAuthorizedRepresentative() throws Exception {
        verifyUpdateARStateBehavior("/api-registry/authorised-representatives.suspend", ARUpdateActionType.SUSPEND);
    }

    @Test
    void restoreAuthorizedRepresentative() throws Exception {
        verifyUpdateARStateBehavior("/api-registry/authorised-representatives.restore", ARUpdateActionType.RESTORE);
    }

    @Test
    void changeARAccessRights() throws Exception {
        verifyUpdateARAccessRightsBehaviour("/api-registry/authorised-representatives.change.access-rights", ARUpdateActionType.CHANGE_ACCESS_RIGHTS);
    }

    @Test
    void replaceAuthorizedRepresentative() throws Exception {
        // given
        String url = "/api-registry/authorised-representatives.replace";
        ObjectMapper mapper = new ObjectMapper();
        ReplaceARRequest request = new ReplaceARRequest();
        request.setCandidateUrid(candidateUrid);

        // when then
        mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        // given
        request.setAccessRight(accountAccessRight);

        // when then
        mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        // given
        request.setReplaceeUrid(replaceeUrid);

        // when then
        String response = mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(requestId.toString(), response);
        then(authorizedRepresentativeService).should(times(1)).placeUpdateRequest(arUpdateActionDTO.capture());
        ARUpdateActionDTO dto = arUpdateActionDTO.getValue();
        verifyCommonUpdateARBehaviour(dto, ARUpdateActionType.REPLACE);
        assertEquals(dto.getCandidate().getRight(), accountAccessRight);
        assertNotNull(dto.getCandidate());
        assertEquals(request.getCandidateUrid(), dto.getCandidate().getUrid());
    }

    private void verifyUpdateARAccessRightsBehaviour(String url, ARUpdateActionType expectedType)
        throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        UpdateARRightsRequest request = new UpdateARRightsRequest();
        request.setAccessRight(accountAccessRight);
        request.setCandidateUrid(null);

        // when then
        mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        //given
        request.setCandidateUrid(candidateUrid);
        request.setAccessRight(null);
        // when then
        mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        // given
        request.setAccessRight(accountAccessRight);
        // when
        MvcResult result = mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn();

        Long response = Long.valueOf(result.getResponse().getContentAsString());
        // then
        assertEquals(requestId, response);
        then(authorizedRepresentativeService).should(times(1)).placeUpdateRequest(arUpdateActionDTO.capture());
        ARUpdateActionDTO dto = arUpdateActionDTO.getValue();
        verifyCommonUpdateARBehaviour(dto, expectedType);
        assertEquals(dto.getCandidate().getRight(), accountAccessRight);
    }

    private void verifyUpdateARStateBehavior(String url, ARUpdateActionType expectedType) throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        UpdateARRequest request = new UpdateARRequest();
        // when then
        mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        // given
        request.setCandidateUrid(candidateUrid);
        // when
        MvcResult result = mockMvc.perform(post(url)
            .accept(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn();
        // then
        assertEquals(requestId.toString(), result.getResponse().getContentAsString());
        then(authorizedRepresentativeService).should(times(1)).placeUpdateRequest(arUpdateActionDTO.capture());
        ARUpdateActionDTO dto = arUpdateActionDTO.getValue();
        verifyCommonUpdateARBehaviour(dto, expectedType);
    }

    private void verifyCommonUpdateARBehaviour(ARUpdateActionDTO dto, ARUpdateActionType type) {
        assertNotNull(dto);
        assertEquals(accountId, dto.getAccountIdentifier());
        assertNotNull(dto.getCandidate());
        assertEquals(candidateUrid, dto.getCandidate().getUrid());
        assertEquals(type, dto.getUpdateType());
    }
}