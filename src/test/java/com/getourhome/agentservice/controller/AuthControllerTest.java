package com.getourhome.agentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getourhome.agentservice.dto.request.LoginRequestDto;
import com.getourhome.agentservice.dto.request.UserRegisterDto;
import com.getourhome.agentservice.entity.RegistrationStatus;
import com.getourhome.agentservice.entity.User;
import com.getourhome.agentservice.service.AuthService;
import com.getourhome.agentservice.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    AuthControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("사용자 회원가입 - 정상 회원가입")
    @Test
    void givenUserRegisterDto_whenValidRequest_thenReturnCreated() throws Exception{
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto
                .newBuilder()
                .userId("tester")
                .username("김테스트")
                .phoneNumber("01012341234")
                .registrationNumber("11111-0000-1111")
                .agencyName("테스트 공인중개사")
                .password("tester1234")
                .email("tester@test.com")
                .build();


        // when & then
        mvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                        .andExpect(status().isCreated());
        then(authService).should().registerUser(any(UserRegisterDto.class));
    }

    @DisplayName("사용자 회원가입 - 중복 아이디, 회원가입 거부")
    @Test
    void givenUserRegisterDto_whenUserIdExists_thenReturnBadRequest() throws Exception{
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto
                .newBuilder()
                .userId("tester")
                .username("김테스트2")
                .phoneNumber("01011111111")
                .registrationNumber("11111-0000-1111")
                .agencyName("테스트 공인중개사")
                .password("tester1234")
                .email("tester2@test.com")
                .build();

        when(authService.findByUserId("tester")).thenReturn(Optional.of(new User()));

        // when & then
        mvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isBadRequest());
    }


    @DisplayName("사용자 회원가입 - 중복 이메일, 회원가입 거부")
    @Test
    void givenUserRegisterDto_whenEmailExists_thenReturnBadRequest() throws Exception{
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto
                .newBuilder()
                .userId("tester")
                .username("김테스트2")
                .phoneNumber("01011111111")
                .agencyName("테스트 공인중개사")
                .registrationNumber("11111-0000-1111")
                .password("tester1234")
                .email("tester2@test.com")
                .build();

        when(authService.findByEmail("tester2@test.com")).thenReturn(Optional.of(new User()));

        // when & then
        mvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("사용자 회원가입 - 중복 공인중개사 등록 번호, 회원가입 거부")
    @Test
    void givenUserRegisterDto_whenRegistrationNumberExists_thenReturnBadRequest() throws Exception{
        // Given
        UserRegisterDto userRegisterDto = UserRegisterDto
                .newBuilder()
                .userId("tester")
                .username("김테스트2")
                .phoneNumber("01011111111")
                .agencyName("테스트 공인중개사")
                .registrationNumber("11111-0000-1111")
                .password("tester1234")
                .email("tester2@test.com")
                .build();

        when(authService.findByRegistrationNumber("11111-0000-1111")).thenReturn(Optional.of(new User()));

        // when & then
        mvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("사용자 로그인 - 정상 로그인")
    @Test
    void givenLoginRequestDto_whenValidCredentials_thenReturnOk() throws Exception{
        // Given
        LoginRequestDto loginRequestDto = LoginRequestDto
                .builder()
                .userId("tester")
                .password("test123")
                .build();

        User user = new User();
        user.setRegistrationStatus(RegistrationStatus.ACCEPTED);

        when(authService.login(any(LoginRequestDto.class))).thenReturn(user);
        when(jwtTokenProvider.createToken(any(UUID.class), any(String.class))).thenReturn("jwtToken");

        // When & Then
        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("가입 승인되지 않은 사용자 로그인 - 로그인 실패")
    @Test
    void givenLoginRequestDto_whenUnAcceptCredentials_thenReturnUnAuth() throws Exception{
        // Given
        LoginRequestDto loginRequestDto = LoginRequestDto
                .builder()
                .userId("tester")
                .password("test123")
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(new User());
        when(jwtTokenProvider.createToken(any(UUID.class), any(String.class))).thenReturn("jwtToken");

        // When & Then
        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("사용자 로그인 - 존재하지 않는 아이디 혹은 비밀번호, 로그인 거부")
    @Test
    void givenLoginRequestDto_whenInvalidCredentials_thenReturnBadRequest() throws Exception{
        // Given
        LoginRequestDto loginRequestDto = LoginRequestDto
                .builder()
                .userId("tester")
                .password("test123")
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(null);

        // When & Then
        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());
    }
}