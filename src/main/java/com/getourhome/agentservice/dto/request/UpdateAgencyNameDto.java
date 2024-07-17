package com.getourhome.agentservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "공인중개사 이름변경 요청 DTO")
public class UpdateAgencyNameDto {
    @NotBlank(message = "공인중개사 상호명을 입력해주세요")
    @JsonProperty("agency_name")
    @Schema(description = "공인중개사 상호명", example = "김테스트 공인중개사")
    private String agencyName;
}
