package com.getourhome.agentservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "로그인 요청 DTO")
public class LoginRequestDto {
    @NotBlank(message = "사용자 아이디를 입력해주세요")
    @JsonProperty("user_id")
    @Schema(description = "사용자 아이디", example = "testerkim")
    private String userId;
    @NotBlank(message = "사용자 비밀번호를 입력해주세요")
    @Schema(description = "사용자 비밀번호", example = "tester123")
    private String password;
}
