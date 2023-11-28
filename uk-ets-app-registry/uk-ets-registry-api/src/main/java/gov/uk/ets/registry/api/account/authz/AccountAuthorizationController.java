package gov.uk.ets.registry.api.account.authz;

import gov.uk.ets.registry.api.account.web.model.AccountAccessDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for account access authorization.
 */
@Tag(name = "Account Authorization")
@RestController
@RequestMapping(path = "/api-registry/account/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class AccountAuthorizationController {

    /**
     * Service for account authorization.
     */
    private AccountAuthorizationService accountAuthorizationService;

    public AccountAuthorizationController(AccountAuthorizationService accountAuthorizationService) {
        this.accountAuthorizationService = accountAuthorizationService;
    }

    /**
     * Retrieves account access rights for the current user.
     *
     * @return the current user access rights on the account.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AccountAccessDTO>> getAccountAccesses() {
        return new ResponseEntity<>(accountAuthorizationService.getAccountAccessesForCurrentUser(), HttpStatus.OK);
    }
}
