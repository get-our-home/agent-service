package com.getourhome.agentservice.service;

import com.getourhome.agentservice.dto.request.RejectRegistrationRequestDto;
import com.getourhome.agentservice.entity.RegistrationStatus;
import com.getourhome.agentservice.entity.User;
import com.getourhome.agentservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagementService {
    private final UserRepository userRepository;

    public User acceptUser(UUID uuid) {
        User user = userRepository.findById(uuid).orElse(null);
        if (user == null) {
            return null;
        }
        user.setRegistrationStatus(RegistrationStatus.ACCEPTED);
        return userRepository.save(user);
    }

    public User rejectUser(UUID uuid, RejectRegistrationRequestDto request) {
        User user = userRepository.findById(uuid).orElse(null);
        if (user == null) {
            return null;
        }
        user.setRegistrationStatus(RegistrationStatus.REJECTED);
        user.setRejectReason(request.getReason());
        return userRepository.save(user);
    }
}
