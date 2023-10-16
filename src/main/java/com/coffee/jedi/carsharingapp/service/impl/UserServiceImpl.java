package com.coffee.jedi.carsharingapp.service.impl;

import com.coffee.jedi.carsharingapp.dto.user.UpdateUserDto;
import com.coffee.jedi.carsharingapp.dto.user.UpdateUserRoleDto;
import com.coffee.jedi.carsharingapp.dto.user.UserDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.coffee.jedi.carsharingapp.exception.EntityNotFoundException;
import com.coffee.jedi.carsharingapp.exception.RegistrationException;
import com.coffee.jedi.carsharingapp.mapper.UserMapper;
import com.coffee.jedi.carsharingapp.model.Role;
import com.coffee.jedi.carsharingapp.model.User;
import com.coffee.jedi.carsharingapp.repository.RoleRepository;
import com.coffee.jedi.carsharingapp.repository.UserRepository;
import com.coffee.jedi.carsharingapp.service.UserService;
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
