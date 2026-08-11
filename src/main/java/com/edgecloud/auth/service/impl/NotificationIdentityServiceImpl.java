package com.edgecloud.auth.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edgecloud.auth.dto.NotificationIdentityRequest;
import com.edgecloud.auth.dto.NotificationIdentityResponse;
import com.edgecloud.auth.repository.UserRepository;
import com.edgecloud.auth.service.NotificationIdentityService;

@Service
public class NotificationIdentityServiceImpl implements NotificationIdentityService {

    private final UserRepository userRepository;

    public NotificationIdentityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationIdentityResponse> resolve(NotificationIdentityRequest request) {
        return userRepository.findByIdInOrderByIdAsc(request.userIds().stream().distinct().toList()).stream()
                .map(user -> new NotificationIdentityResponse(user.getId(), user.getEmail(), user.getRole()))
                .toList();
    }
}
