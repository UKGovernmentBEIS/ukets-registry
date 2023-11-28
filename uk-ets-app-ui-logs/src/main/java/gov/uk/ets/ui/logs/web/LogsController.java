package gov.uk.ets.ui.logs.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-ui-logs")
@RequiredArgsConstructor
@Log4j2
public class LogsController {

    private final JsonSanitizerService jsonSanitizerService;

    @PostMapping(path = "logs.submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> requestReportFromClient(@RequestBody String jsonLogEventsBody) {
        var sanitizedJson = jsonSanitizerService.createAndSanitizeLogs(jsonLogEventsBody);
        sanitizedJson.forEach(message -> log.info(message));
        return ResponseEntity.accepted().build();
    }
}
