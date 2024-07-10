package com.getourhome.agentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getourhome.agentservice.dto.request.RejectRegistrationRequestDto;
import com.getourhome.agentservice.entity.User;
import com.getourhome.agentservice.service.ManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagementController.class)
class ManagementControllerTest {
    @MockBean
    private ManagementService managementService;

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    ManagementControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("공인중개사 가입 요청 승인")
    @Test
    void givenAcceptRegistration_whenValidRequest_thenReturnOk() throws Exception{
        // Given
        UUID uuid = UUID.randomUUID();
        User mockUser = User
                .builder()
                .id(uuid)
                .build();
        given(managementService.acceptUser(uuid)).willReturn(mockUser);

        // when & then
        mvc.perform(patch("/admin/registrations/" + uuid + "/accept"))
                .andExpect(status().isOk());
        then(managementService).should().acceptUser(uuid);
    }

    @DisplayName("존재하지 않는 아이디 입력시 공인중개사 가입 요청 에러 발생")
    @Test
    void givenAcceptRegistration_whenUserNotFound_thenReturnBadRequest() throws Exception{
        // Given
        UUID uuid = UUID.randomUUID();
        given(managementService.acceptUser(uuid)).willReturn(null);


        // when & then
        mvc.perform(patch("/admin/registrations/" + uuid + "/accept"))
                .andExpect(status().isBadRequest());
        then(managementService).should().acceptUser(uuid);
    }

    @DisplayName("공인중개사 가입 요청 거절")
    @Test
    void givenRejectRegistration_whenValidRequest_thenReturnOk() throws Exception{
        // Given
        RejectRegistrationRequestDto requestDto = RejectRegistrationRequestDto
                .builder().reason("거부 사유").build();

        UUID uuid = UUID.randomUUID();
        User mockUser = User
                .builder()
                .id(uuid)
                .build();
        given(managementService.rejectUser(eq(uuid), any(RejectRegistrationRequestDto.class))).willReturn(mockUser);

        // when & then
        mvc.perform(
                        patch("/admin/registrations/" + uuid + "/reject")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk());
        then(managementService).should().rejectUser(eq(uuid), any(RejectRegistrationRequestDto.class));
    }

    @DisplayName("존재하지 않는 아이디 입력시 공인중개사 거절 요청 에러 발생")
    @Test
    void givenRejectRegistration_whenUserNotFound_thenReturnBadRequest() throws Exception{
        // Given
        RejectRegistrationRequestDto requestDto = RejectRegistrationRequestDto
                .builder().reason("거부 사유").build();

        UUID uuid = UUID.randomUUID();
        given(managementService.rejectUser(eq(uuid), any(RejectRegistrationRequestDto.class))).willReturn(null);

        // when & then
        mvc.perform(
                        patch("/admin/registrations/" + uuid + "/reject")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isBadRequest());
        then(managementService).should().rejectUser(eq(uuid), any(RejectRegistrationRequestDto.class));
    }
}