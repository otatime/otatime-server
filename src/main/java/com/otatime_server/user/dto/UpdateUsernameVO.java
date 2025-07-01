package com.otatime_server.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateUsernameVO(

        @Length(min = 2, max = 20, message = "사용자 이름은 2자 이상 20자 이내로 입력해주세요.")
        @NotBlank(message = "사용자 이름은 필수 입력 값입니다.")
        String username

) {
}
