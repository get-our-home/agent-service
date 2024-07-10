package com.getourhome.agentservice.service;

import com.getourhome.agentservice.dto.request.RejectRegistrationRequestDto;
import com.getourhome.agentservice.entity.RegistrationStatus;
import com.getourhome.agentservice.entity.User;
import com.getourhome.agentservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(properties = {
        "security.jwt.token.secret-key=mySecretKeymySecretKeymySecretKeymySecretKeymySecretKey",
        "security.jwt.token.expire-length=3600000"
})
@AutoConfigureMockMvc
class ManagementServiceTest {
    @Autowired
    private ManagementService managementService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @DisplayName("사용자 가입 요청 승인 성공시 DB 정상 반영 테스트")
    @Test
    void givenAcceptUser_whenValidUuid_thenReturnUpdatedUser() {
        // Given
        UUID uuid = UUID.randomUUID();
        User mockUser = User.builder()
                .id(uuid)
                .registrationStatus(RegistrationStatus.PENDING)
                .build();
        given(userRepository.findById(uuid)).willReturn(Optional.of(mockUser));
        given(userRepository.save(any(User.class))).willReturn(mockUser);

        // When
        managementService.acceptUser(uuid);

        // Then
        Optional<User> userOptional = userRepository.findById(uuid);
        assertThat(userOptional).isPresent();
        User user = userOptional.get();
        assertThat(user.getRegistrationStatus()).isEqualTo(RegistrationStatus.ACCEPTED);
    }

    @DisplayName("존재하지 않는 사용자 uuid accept 요청시 null 응답")
    @Test
    void givenAcceptUser_whenInvalidUuid_thenReturnNull() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        User user = managementService.acceptUser(uuid);

        // Then
        assertThat(user).isNull();
    }

    @DisplayName("사용자 가입 요청 거부 성공시 DB 정상 반영 테스트")
    @Test
    void givenRejectUser_whenValidUuid_thenReturnUpdatedUser() {
        // Given
        UUID uuid = UUID.randomUUID();
        User mockUser = User.builder()
                .id(uuid)
                .registrationStatus(RegistrationStatus.PENDING)
                .build();
        RejectRegistrationRequestDto requestDto = RejectRegistrationRequestDto
                .builder().reason("거부 사유").build();
        given(userRepository.findById(uuid)).willReturn(Optional.of(mockUser));
        given(userRepository.save(any(User.class))).willReturn(mockUser);

        // When
        managementService.rejectUser(uuid, requestDto);

        // Then
        Optional<User> userOptional = userRepository.findById(uuid);
        assertThat(userOptional).isPresent();
        User user = userOptional.get();
        assertThat(user.getRegistrationStatus()).isEqualTo(RegistrationStatus.REJECTED);
        assertThat(user.getRejectReason()).isEqualTo(requestDto.getReason());
    }

    @DisplayName("존재하지 않는 사용자 uuid reject 요청시 null 응답")
    @Test
    void givenRejectUser_whenInvalidUuid_thenReturnNull() {
        // Given
        UUID uuid = UUID.randomUUID();
        RejectRegistrationRequestDto requestDto = RejectRegistrationRequestDto
                .builder().reason("거부 사유").build();

        // When
        User user = managementService.rejectUser(uuid, requestDto);

        // Then
        assertThat(user).isNull();
    }
}