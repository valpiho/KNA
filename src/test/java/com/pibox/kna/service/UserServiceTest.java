package com.pibox.kna.service;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.User;
import com.pibox.kna.exceptions.domain.EmailExistException;
import com.pibox.kna.exceptions.domain.UserNotFoundException;
import com.pibox.kna.exceptions.domain.UsernameExistException;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    UserDTO userDtoAsDriver= UserDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@gmail.com")
            .username("johnDoe")
            .isClientOrDriver(false)
            .driverPlateNumber("323 BDX")
            .build();

    UserDTO userDtoAsClient= UserDTO.builder()
            .firstName("Val")
            .lastName("Piho")
            .email("val.piho@gmail.com")
            .username("valPiho")
            .isClientOrDriver(true)
            .clientEmail("company@gmail.com")
            .clientPhoneNumber("4343490332")
            .clientCountry("Italy")
            .clientCity("Roma")
            .clientStreetAddress("Alloha 12")
            .clientZipCode("32321")
            .build();

    @Test
    void registerNewUserAsDriver() throws UserNotFoundException, EmailExistException, MessagingException, UsernameExistException {
        userService.register(userDtoAsDriver);
        User user = userRepository.findUserByUsername(userDtoAsDriver.getUsername());

        assertThat(user.getUsername()).isEqualTo(userDtoAsDriver.getUsername());
        assertThat(user.getDriver().getPlateNumber()).isEqualTo(userDtoAsDriver.getDriverPlateNumber());
        assertThat(user.getRole()).isEqualTo(Role.ROLE_DRIVER.toString());
        assertThat(user.isActive()).isTrue();
        assertThat(user.getClient()).isNull();
    }

    @Test
    void registerNewUserAsClient() throws UserNotFoundException, EmailExistException, MessagingException, UsernameExistException {
        userService.register(userDtoAsClient);
        User user = userRepository.findUserByUsername(userDtoAsClient.getUsername());

        assertThat(user.getUsername()).isEqualTo(userDtoAsClient.getUsername());
        assertThat(user.getClient().getEmail()).isEqualTo(userDtoAsClient.getClientEmail());
        assertThat(user.getRole()).isEqualTo(Role.ROLE_CLIENT.toString());
        assertThat(user.isActive()).isTrue();
        assertThat(user.getDriver()).isNull();
    }

    @Test
    void updateUserAsDriver() throws UserNotFoundException, EmailExistException, UsernameExistException {
        UserDTO updatedUserDto = UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .username("johnDo")
                .isClientOrDriver(false)
                .driverPlateNumber("321 BDX")
                .build();

        User capturedUpdatedUser = userService.updateUser("johnDoe", updatedUserDto);

        assertThat(capturedUpdatedUser.getUsername()).isEqualTo(updatedUserDto.getUsername());
        assertThat(capturedUpdatedUser.getDriver().getPlateNumber()).isEqualTo(updatedUserDto.getDriverPlateNumber());
        assertThat(capturedUpdatedUser.getRole()).isEqualTo(Role.ROLE_DRIVER.toString());
        assertThat(capturedUpdatedUser.isActive()).isTrue();
        assertThat(capturedUpdatedUser.getClient()).isNull();
    }

    @Test
    void updateUserAsClient() throws UserNotFoundException, EmailExistException, UsernameExistException {
        UserDTO updatedUserDto = UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("val.piho@gmail.com")
                .username("valP")
                .isClientOrDriver(true)
                .clientEmail("company1@gmail.com")
                .clientPhoneNumber("4343490332")
                .clientCountry("Italy")
                .clientCity("Roma")
                .clientStreetAddress("Alloha 12")
                .clientZipCode("32321")
                .build();

        User capturedUpdatedUser = userService.updateUser("valPiho", updatedUserDto);

        assertThat(capturedUpdatedUser.getUsername()).isEqualTo(updatedUserDto.getUsername());
        assertThat(capturedUpdatedUser.getClient().getEmail()).isEqualTo(updatedUserDto.getClientEmail());
        assertThat(capturedUpdatedUser.getRole()).isEqualTo(Role.ROLE_CLIENT.toString());
        assertThat(capturedUpdatedUser.isActive()).isTrue();
        assertThat(capturedUpdatedUser.getDriver()).isNull();
    }

    @Test
    void resetPassword() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void findUserByUsername() {
    }

    @Test
    void findUserByEmail() {
    }

    @Test
    void findAllUsers() {
        List<User> users = userService.findAllUsers("johnDo");

        assertFalse(users.isEmpty());
        assertEquals("valP", users.get(0).getUsername());
    }
}