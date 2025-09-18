package gov.uk.ets.registry.api.accountholder.web.model;

import static org.junit.jupiter.api.Assertions.*;


import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountHolderChangeDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validateAccountHolderChangeDTO_shouldFailOnMissingFields() {
        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();

        Set<ConstraintViolation<AccountHolderChangeDTO>> violations = validator.validate(dto);

        assertEquals(3, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accountIdentifier")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("acquiringAccountHolder")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("acquiringAccountHolderContactInfo")));
    }

    @Test
    void validateAccountHolderChangeDTO_shouldPassWithValidFields() {
        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(123L);
        dto.setAcquiringAccountHolder(new AccountHolderDTO());
        dto.setAcquiringAccountHolderContactInfo(new AccountHolderRepresentativeDTO());

        Set<ConstraintViolation<AccountHolderChangeDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
