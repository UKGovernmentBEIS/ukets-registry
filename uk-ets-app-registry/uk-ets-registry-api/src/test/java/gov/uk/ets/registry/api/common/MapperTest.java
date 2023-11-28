package gov.uk.ets.registry.api.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.profile.service.EmailChangeDTO;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class MapperTest {

    Mapper mapper = new Mapper(new ObjectMapper());

    @Test
    void convertToJson() {
        EmailChangeDTO emailChangeDTO = EmailChangeDTO.builder()
            .urid("urid")
            .newEmail("newEmail")
            .oldEmail("oldEmail")
            .build();
        String s = mapper.convertToJson(emailChangeDTO);
        assertEquals("{\"urid\":\"urid\",\"newEmail\":\"newEmail\",\"oldEmail\":\"oldEmail\"}", s);
    }

    @Test
    void convertToPojo() {
        String s = "{\"requestId\":1,\"type\":\"USER_DETAILS_UPDATE_REQUEST\"," +
            "\"initiatedDate\":1641289179459, \"status\":\"SUBMITTED_NOT_YET_APPROVED\"}";
        Task task = mapper.convertToPojo(s, Task.class);
        assertEquals(1L, task.getRequestId());
        assertEquals(RequestType.USER_DETAILS_UPDATE_REQUEST, task.getType());
        assertEquals(Date.from(Instant.ofEpochMilli(1641289179459L)), task.getInitiatedDate());
        assertEquals(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, task.getStatus());
    }

    @Test
    void convertToPojo_exception1() {
        assertThrows(JsonMappingException.class, () -> mapper.convertToPojo(null, Task.class));
    }

    @Test
    void convertToPojo_exception2() {
        assertThrows(JsonMappingException.class, () -> mapper.convertToPojo("{requestId:1}", Task.class));
    }

}
