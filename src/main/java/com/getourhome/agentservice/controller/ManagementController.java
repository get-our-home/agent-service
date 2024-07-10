package com.getourhome.agentservice.controller;

import com.getourhome.agentservice.dto.request.RejectRegistrationRequestDto;
import com.getourhome.agentservice.dto.response.BaseResponseDto;
import com.getourhome.agentservice.entity.User;
import com.getourhome.agentservice.service.ManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Management API", description = "공인중개사 가입요청 관리에 대한 API입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/registrations")
public class ManagementController {
    private final ManagementService managementService;

    @PatchMapping("/{agentId}/accept")
    @Operation(
            summary = "공인중개사 가입 요청 승인",
            description = "회원가입 후 가입 승인 대기 상태인 공인중개사의 가입을 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전환 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponseDto.class)) }),
            @ApiResponse(responseCode = "400", description = "찾을 수 없는 agent user uuid",
                    content = @Content)})
    public ResponseEntity<?> acceptRegistration(
            @Parameter(description = "공인중개사 고유 식별 아이디",
                    example = "a1d28840-ec14-4f97-ae81-4c8fee84167e")
            @PathVariable("agentId") String agentId
    ) {
        UUID uuid = UUID.fromString(agentId);

        User user = managementService.acceptUser(uuid);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "사용자 고유 아이디에 맞는 사용자가 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        String msg = user.getUserId() + " 가입 승인";
        BaseResponseDto baseResponseDto = BaseResponseDto.builder().message(msg).build();
        return ResponseEntity.status(HttpStatus.OK).body(baseResponseDto);
    }

    @PatchMapping("/{agentId}/reject")
    @Operation(
            summary = "공인중개사 가입 요청 거부",
            description = "회원가입 후 가입 승인 대기 상태인 공인중개사의 가입을 거부합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전환 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponseDto.class)) }),
            @ApiResponse(responseCode = "400", description = "찾을 수 없는 agent user uuid",
                    content = @Content)})
    public ResponseEntity<?> rejectRegistration(
            @Parameter(description = "공인중개사 고유 식별 아이디",
                    example = "a1d28840-ec14-4f97-ae81-4c8fee84167e")
            @PathVariable("agentId") String agentId,
            @RequestBody RejectRegistrationRequestDto request
    ) {
        UUID uuid = UUID.fromString(agentId);

        User user = managementService.rejectUser(uuid, request);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "사용자 고유 아이디에 맞는 사용자가 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        String msg = user.getUserId() + " 가입 거부 \n사유 : " + request.getReason();
        BaseResponseDto baseResponseDto = BaseResponseDto.builder().message(msg).build();
        return ResponseEntity.status(HttpStatus.OK).body(baseResponseDto);
    }
}
