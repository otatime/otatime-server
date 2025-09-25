package com.otatime_server.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record JoinRequest(

        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
                message = "비밀번호는 8~20자 영문자+숫자를 조합해야 합니다."
        )
        String password,

        @Length(min = 2, max = 20, message = "사용자 이름은 2자 이상 20자 이내로 입력해주세요.")
        @NotBlank(message = "사용자 이름은 필수 입력 값입니다.")
        String username,

        String profileImageUrl,

        String certToken

) {
}
