package org.artanddecor.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldAllowEmptyOptionalPhoneNumber() {
        UserDto dto = UserDto.builder()
                .userName("Bigboss")
                .email("Bigboss@gmail.com")
                .firstName("Le")
                .lastName("Cong Nghia")
                .password("123abc!!!")
                .phoneNumber("")
                .imageAvatarName("")
                .userEnabled(true)
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), () -> violations.toString());
    }

    @Test
    void shouldRejectInvalidPhoneNumberWhenProvided() {
        UserDto dto = UserDto.builder()
                .userName("Bigboss")
                .email("Bigboss@gmail.com")
                .phoneNumber("123")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
