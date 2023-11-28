package gov.uk.ets.registry.api.user.forgot.passwd.web;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(of = {"token", "otp", "newPasswd"})
public class ResetPasswordRequest {

    @NotBlank(message = "Token cannot be empty.")
    String token;

    @NotNull
    @Pattern(regexp = "\\d{6}", message = "OTP code consists of 6 numbers.")
    String otp;

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    String newPasswd;
}
