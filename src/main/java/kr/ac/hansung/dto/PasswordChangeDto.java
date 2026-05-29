package kr.ac.hansung.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {

    @NotBlank(message = "현재 비밀번호를 입력하세요")
    private String currentPassword;

    @NotBlank @Size(min = 8, message = "비밀번호는 최소 8자리여야 합니다")
    private String newPassword;

    @NotBlank(message = "새 비밀번호를 다시 입력해주세요")
    private String confirmNewPassword;
}
