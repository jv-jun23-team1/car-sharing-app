package com.team01.carsharingapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    private static final String EMAIL = "email@example.com";
    private static final String NEW_EMAIL = "new.email@example.com";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String HASHED_PASSWORD = "hashedPassword";
    private static final String FIRST_NAME = "FirstName";
    private static final String NEW_FIRST_NAME = "NewFirstName";
    private static final String LAST_NAME = "LastName";
    private static final String NEW_LAST_NAME = "NewLastName";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("Register user with valid data")
    void register_validData_returnUserRegistrationResponseDto() throws RegistrationException {
        UserRegistrationRequestDto requestDto = getUserRegistrationRequestDto();
        User user = getValidUser();

        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(roleRepository.getRolesByName(Role.RoleName.ROLE_CUSTOMER)).thenReturn(new Role());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user))
                .thenReturn(getUserRegistrationResponseDtoFromUser(user));

        UserRegistrationResponseDto expected = getUserRegistrationResponseDto();
        UserRegistrationResponseDto actual = userServiceImpl.register(requestDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponseDto(user);
    }

    @Test
    @DisplayName("Register user with existing userName")
    void register_userAlreadyExist_throwException() {
        UserRegistrationRequestDto requestDto = getUserRegistrationRequestDto();

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userServiceImpl.register(requestDto));

        assertEquals("Can't register user with email: email@example.com", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("Get info about user")
    void getInfo_validUser_returnUserDto() {
        User user = getValidUser();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(getUserDto());

        UserDto expectedDto = getUserDtoFromUser(user);
        UserDto actualDto = userServiceImpl.getInfo(user);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void getInfo_UserNotFound_valid_throwException() {
        User user = getValidUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userServiceImpl.getInfo(user);
        });

        String expectedMessage = "Can't find user with id: " + ID_ONE;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void updateRoles_validUpdateUserRoleDto_returnUserDto() {
        Role role1 = new Role();
        role1.setId(ID_ONE);
        Role role2 = new Role();
        role2.setId(ID_TWO);
        User existingUser = getValidUser();
        UserDto userDto = getUserDto();
        userDto.setRoleIds(Set.of(1L, 2L));
        UpdateUserRoleDto updateRoleDto = getUpdateUserRoleDto();
        Long userId = ID_ONE;

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findById(ID_ONE)).thenReturn(Optional.of(role1));
        when(roleRepository.findById(ID_TWO)).thenReturn(Optional.of(role2));
        when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(userMapper.toDto(existingUser)).thenReturn(userDto);

        UserDto expectedUserDto = getUserDtoFromUpdateUserRoleDto(getUpdateUserRoleDto());
        UserDto actual = userServiceImpl.updateRoles(ID_ONE, updateRoleDto);

        assertNotNull(actual);
        assertEquals(expectedUserDto, actual);
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(2)).findById(anyLong());
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).toDto(existingUser);
    }

    @Test
    void updateRoles_inValidUpdateUserRoleDto_throwException() {
        Long userId = ID_ONE;
        UpdateUserRoleDto updateUserRoleDto = new UpdateUserRoleDto(Set.of(ID_ONE));
        User existingUser = getValidUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userServiceImpl.updateRoles(userId, updateUserRoleDto));

        String expectedMessage = "Can't get role by id: 1";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findById(any());
        verifyNoMoreInteractions(userRepository, roleRepository);
    }

    @Test
    void update_validUpdateUserDto_returnUserDto() {
        UpdateUserDto updateUserDto = getUpdateUserDto();
        Long userId = ID_ONE;
        User existingUser = getValidUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(any(String.class))).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userMapper.toDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return getUserDtoFromUser(user);
        });

        UserDto expected = getUserDtoFromUpdateUserDto(updateUserDto);
        expected.setId(userId);
        UserDto actual = userServiceImpl.update(updateUserDto, existingUser);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(NEW_EMAIL, savedUser.getEmail());
        assertEquals(NEW_FIRST_NAME, savedUser.getFirstName());
        assertEquals(NEW_LAST_NAME, savedUser.getLastName());
        assertEquals(HASHED_PASSWORD, savedUser.getPassword());
        verify(userMapper, times(1)).toDto(savedUser);
    }

    @Test
    void update_UserNotFound_throwException() {
        UpdateUserDto updateUserDto = getUpdateUserDto();
        User user = new User();
        user.setId(ID_ONE);

        when(userRepository.findById(ID_ONE)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userServiceImpl.update(updateUserDto, user);
        });

        assertEquals("Can't find user with id: " + ID_ONE, exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    private Role getRoleCustomer() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_CUSTOMER);
        return role;
    }

    private UserDto getUserDtoFromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoleIds(user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet()));
        return userDto;
    }

    private UserRegistrationRequestDto getUserRegistrationRequestDto() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(EMAIL);
        requestDto.setPassword(PASSWORD);
        requestDto.setRepeatPassword(PASSWORD);
        requestDto.setFirstName(FIRST_NAME);
        requestDto.setLastName(LAST_NAME);
        return requestDto;
    }

    private User getValidUser() {
        User user = new User();
        user.setId(ID_ONE);
        user.setEmail(EMAIL);
        user.setPassword(HASHED_PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setRoles(Set.of(getRoleCustomer()));
        return user;
    }

    private UserRegistrationResponseDto getUserRegistrationResponseDto() {
        UserRegistrationResponseDto userRegistrationResponseDto = new UserRegistrationResponseDto();
        userRegistrationResponseDto.setId(ID_ONE);
        userRegistrationResponseDto.setEmail(EMAIL);
        userRegistrationResponseDto.setFirstName(FIRST_NAME);
        userRegistrationResponseDto.setLastName(LAST_NAME);
        return userRegistrationResponseDto;
    }

    private UserRegistrationResponseDto getUserRegistrationResponseDtoFromUser(User user) {
        UserRegistrationResponseDto userRegistrationResponseDto = new UserRegistrationResponseDto();
        userRegistrationResponseDto.setId(user.getId());
        userRegistrationResponseDto.setEmail(user.getEmail());
        userRegistrationResponseDto.setFirstName(user.getFirstName());
        userRegistrationResponseDto.setLastName(user.getLastName());
        return userRegistrationResponseDto;
    }

    private UpdateUserDto getUpdateUserDto() {
        UpdateUserDto updateUserDto =
                new UpdateUserDto();
        updateUserDto.setEmail(NEW_EMAIL);
        updateUserDto.setFirstName(NEW_FIRST_NAME);
        updateUserDto.setLastName(NEW_LAST_NAME);
        updateUserDto.setPassword(NEW_PASSWORD);
        return updateUserDto;
    }

    private UserDto getUserDtoFromUpdateUserDto(UpdateUserDto updateUserDto) {
        UserDto userDto = new UserDto();

        userDto.setEmail(updateUserDto.getEmail());
        userDto.setFirstName(updateUserDto.getFirstName());
        userDto.setLastName(updateUserDto.getLastName());

        return userDto;
    }

    private UpdateUserRoleDto getUpdateUserRoleDto() {
        return new UpdateUserRoleDto(Set.of(ID_ONE, ID_TWO));
    }

    private UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(ID_ONE);
        userDto.setEmail(EMAIL);
        userDto.setFirstName(FIRST_NAME);
        userDto.setLastName(LAST_NAME);
        userDto.setRoleIds(Set.of(ID_ONE));
        return userDto;
    }

    private UserDto getUserDtoFromUpdateUserRoleDto(UpdateUserRoleDto updateUserRoleDto) {
        UserDto userDto = getUserDto();
        userDto.setRoleIds(updateUserRoleDto.roleIds());
        return userDto;
    }
}
