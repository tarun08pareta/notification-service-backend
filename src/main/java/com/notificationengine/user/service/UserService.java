package com.notificationengine.user.service;

import com.notificationengine.user.domain.AuthProvider;
import com.notificationengine.user.domain.Role;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.domain.UserStatus;
import com.notificationengine.user.dto.CreateUserRequest;
import com.notificationengine.user.dto.UserResponse;
import com.notificationengine.user.mapper.UserMapper;
import com.notificationengine.user.repository.RoleRepository;
import com.notificationengine.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public UserResponse createUser(CreateUserRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setStatus(UserStatus.ACTIVE);
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "USER role is not configured"
                        )
                );

        user.getRoles().add(userRole);
        User saveUser = userRepository.save(user);
        return userMapper.toResponse(saveUser);
    }
}
