package com.getourhome.agentservice.service;

import com.getourhome.agentservice.dto.request.LoginRequestDto;
import com.getourhome.agentservice.dto.request.UpdateAgencyNameDto;
import com.getourhome.agentservice.dto.request.UserRegisterDto;
import com.getourhome.agentservice.entity.User;
import com.getourhome.agentservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducer kafkaProducer;

    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByRegistrationNumber(String registrationNumber) {
        return userRepository.findByRegistrationNumber(registrationNumber);
    }

    public void registerUser(UserRegisterDto userRegisterDto) {
        User user = userRegisterDto.toEntity(passwordEncoder);
        userRepository.save(user);
    }

    public User login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUserId(loginRequestDto.getUserId()).orElse(null);
        if (user == null || !passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            return null;
        }

        return user;
    }

    public User updateAgencyName(UpdateAgencyNameDto updateAgencyNameDto, UUID agentUuid) {
        User user = userRepository.findById(agentUuid).orElse(null);
        if(user == null) return null;

        user.setAgencyName(updateAgencyNameDto.getAgencyName());
        User updatedUser = userRepository.save(user);
        kafkaProducer.sendAgencyNameChange(user.getUserId(), updateAgencyNameDto.getAgencyName());
        return updatedUser;
    }
}
