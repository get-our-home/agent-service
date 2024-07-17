package com.getourhome.agentservice.controller;

import com.getourhome.agentservice.dto.request.LoginRequestDto;
import com.getourhome.agentservice.dto.request.UpdateAgencyNameDto;
import com.getourhome.agentservice.dto.request.UserRegisterDto;
import com.getourhome.agentservice.dto.response.BaseResponseDto;
import com.getourhome.agentservice.dto.response.UserResponseDto;
import com.getourhome.agentservice.entity.RegistrationStatus;
import com.getourhome.agentservice.entity.User;
import com.getourhome.agentservice.service.AuthService;
import com.getourhome.agentservice.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Auth API", description = "회원가입, 로그인에 대한 API입니다.")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/register")
    @Operation(
            summary = "회원가입",
            description = "유저 이름, 유저 아이디, 비밀번호, 전화번호, 공인중개사 등록번호를 받고 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponseDto.class)) }),
            @ApiResponse(responseCode = "400", description = "유저 ID 중복",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "유저 이메일 중복",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "공인중개사 등록 번호 중복",
                    content = @Content)})
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        if (authService.findByUserId(userRegisterDto.getUserId()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User Id already exists");
            return ResponseEntity.badRequest().body(response);
        }
        if (authService.findByEmail(userRegisterDto.getEmail()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }
        if (authService.findByRegistrationNumber(userRegisterDto.getRegistrationNumber()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Registration Number already exists");
            return ResponseEntity.badRequest().body(response);
        }
        authService.registerUser(userRegisterDto);
        String msg = "회원가입 성공";
        BaseResponseDto baseResponseDto = BaseResponseDto.builder().message(msg).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponseDto);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 아이디, 비밀번호로 로그인을 진행합니다. 로그인이 성공하면 JWT토큰을 전달합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class)) }),
            @ApiResponse(responseCode = "400", description = "사용자 ID 또는 비밀번호를 찾을 수 없음",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "가입 승인되지 않은 공인중개사",
                    content = @Content)})
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        User user = authService.login(loginRequestDto);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(response);
        }

        if(user.getRegistrationStatus() != RegistrationStatus.ACCEPTED){
            Map<String, String> response = new HashMap<>();
            response.put("error", "가입 승인되지 않은 공인중개사입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        UserResponseDto userResponseDto = UserResponseDto
                .builder()
                .role("AGENT")
                .jwt(jwtTokenProvider.createTokenWithoutExpiration(user.getId(), user.getAgencyName()))
                .build();
        return ResponseEntity.ok(userResponseDto);
    }

    @PatchMapping("/update")
    @Operation(summary = "공인중개사 정보 업데이트", description = "공인중개사 상호명 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class)) }),
            @ApiResponse(responseCode = "400", description = "사용자 ID 또는 비밀번호를 찾을 수 없음",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "승인되지 않은 공인중개사",
                    content = @Content)})
    public ResponseEntity<?> updateAgencyName(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateAgencyNameDto updateAgencyNameDto
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = authorizationHeader.substring(7);
        boolean isValidToken = jwtTokenProvider.validateToken(jwtToken);
        if (!isValidToken) {
            return new ResponseEntity<>("Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }

        UUID agentUuid = jwtTokenProvider.getUserPk(jwtToken);
        User user = authService.updateAgencyName(updateAgencyNameDto, agentUuid);

        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(response);
        }

        if(user.getRegistrationStatus() != RegistrationStatus.ACCEPTED){
            Map<String, String> response = new HashMap<>();
            response.put("error", "가입 승인되지 않은 공인중개사입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        UserResponseDto userResponseDto = UserResponseDto
                .builder()
                .role("AGENT")
                .jwt(jwtTokenProvider.createTokenWithoutExpiration(user.getId(), user.getAgencyName()))
                .build();
        return ResponseEntity.ok(userResponseDto);
    }
}
