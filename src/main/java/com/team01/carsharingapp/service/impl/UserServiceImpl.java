package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.dto.user.UpdateUserDto;
import com.team01.carsharingapp.dto.user.UpdateUserRoleDto;
import com.team01.carsharingapp.dto.user.UserDto;
import com.team01.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.team01.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.team01.carsharingapp.exception.EntityNotFoundException;
import com.team01.carsharingapp.exception.RegistrationException;
import com.team01.carsharingapp.mapper.UserMapper;
import com.team01.carsharingapp.model.Role;
import com.team01.carsharingapp.model.User;
import com.team01.carsharingapp.repository.RoleRepository;
import com.team01.carsharingapp.repository.UserRepository;
import com.team01.carsharingapp.service.UserService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        user.setRoles(Set.of(roleRepository.getRolesByName(Role.RoleName.ROLE_CUSTOMER)));
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto getInfo(User user) {
        return userMapper.toDto(getUserById(user.getId()));
    }

    @Override
    @Transactional
    public UserDto updateRoles(Long id, UpdateUserRoleDto updateRoleDto) {
        User user = getUserById(id);
        user.setRoles(updateRoleDto.roleIds().stream()
                .map(ids -> getRoleById(ids))
                .collect(Collectors.toSet()));

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserDto updateUserDto, User user) {
        User userModel = userMapper.toModel(updateUserDto);
        userModel.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        return userMapper.toDto(userRepository.save(userModel));
    }

    private boolean existInDataBase(String username) {
        return userRepository.findByEmail(username).isPresent();
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + id));
    }

    private Role getRoleById(Long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't get role by id: "
                        + id));
    }
}
