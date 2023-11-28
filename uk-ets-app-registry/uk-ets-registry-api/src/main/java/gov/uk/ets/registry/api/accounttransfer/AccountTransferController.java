package gov.uk.ets.registry.api.accounttransfer;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.service.TransferValidationService;
import gov.uk.ets.registry.api.accounttransfer.service.AccountTransferService;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferActionType;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferRequestDTO;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static gov.uk.ets.commons.logging.RequestParamType.DTO;


@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class AccountTransferController {

    private AccountTransferService service;
    private TransferValidationService transferValidationService;

    /**
     * Creates a task for the Account transfer to another account holder.
     *
     * @param request The account transfer request
     * @return The task request id
     */
    @PostMapping(path = "account-transfer.perform", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> accountTransferExistingHolder(@RequestBody @Valid @MDCParam(DTO) AccountTransferRequestDTO request) {
        // TODO BRs should be annotations
        transferValidationService.validateTransferRequestForAccountIdentifier(request.getAccountIdentifier(),
            request.getAcquiringAccountHolder().getId(),
            request.getAccountTransferType().equals(AccountTransferActionType.ACCOUNT_TRANSFER_TO_EXISTING_HOLDER));
        Long taskRequestId = service.accountTransfer(request);
        return new ResponseEntity<>(taskRequestId, HttpStatus.OK);
    }

}
