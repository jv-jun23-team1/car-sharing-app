package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.team01.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.team01.carsharingapp.exception.RegistrationException;
import com.team01.carsharingapp.mapper.UserMapper;
import com.team01.carsharingapp.model.Role;
import com.team01.carsharingapp.model.User;
import com.team01.carsharingapp.repository.RoleRepository;
import com.team01.carsharingapp.repository.UserRepository;
import com.team01.carsharingapp.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (existInDataBase(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user with email: "
                    + requestDto.getEmail());
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(roleRepository.getRolesByName(Role.RoleName.CUSTOMER)));
        return userMapper.toResponseDto(userRepository.save(user));
    }

    public User getCurrentUser() {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean existInDataBase(String username) {
        return userRepository.findByEmail(username).isPresent();
    }
}