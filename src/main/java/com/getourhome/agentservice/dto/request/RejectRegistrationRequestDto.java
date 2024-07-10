package com.getourhome.agentservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "공인중개사 가입 요청 거부 DTO")
public class RejectRegistrationRequestDto {

    @JsonProperty("reason")
    @Schema(description = "가입 요청 거부 이유",
            example = "공인중개사 등록 번호가 유효하지 않습니다.")
    private String reason;
}
