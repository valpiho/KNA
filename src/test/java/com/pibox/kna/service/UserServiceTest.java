package com.pibox.kna.service;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.User;
import com.pibox.kna.exceptions.domain.EmailExistException;
import com.pibox.kna.exceptions.domain.EmailNotFoundException;
import com.pibox.kna.exceptions.domain.UserNotFoundException;
import com.pibox.kna.exceptions.domain.UsernameExistException;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.mail.MessagingException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    void LoadByUsername() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("john"));
    }

    @Test
    void throwsUsernameNotFoundExceptionOnLoadByUsername() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("john"));
    }

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
    void throwsEmailExistsExceptionOnRegisterNewUser() {
        UserDTO user= UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .username("john")
                .isClientOrDriver(false)
                .driverPlateNumber("323 BDX")
                .build();
        assertThrows(EmailExistException.class, () -> userService.register(user));
    }

    @Test
    void throwsUsernameExistsExceptionOnRegisterNewUser() {
        UserDTO user= UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .username("johnDo")
                .isClientOrDriver(false)
                .driverPlateNumber("323 BDX")
                .build();
        assertThrows(UsernameExistException.class, () -> userService.register(user));
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
    void throwsEmailExistsExceptionOnUpdateUser() {
        UserDTO user= UserDTO.builder()
                .email("val.piho@gmail.com")
                .username("john")
                .build();
        assertThrows(EmailExistException.class, () -> userService.updateUser("johnDoe", user));
    }

    @Test
    void throwsUsernameExistsExceptionOnUpdateUser() {
        UserDTO user= UserDTO.builder()
                .email("john@gmail.com")
                .username("valP")
                .build();
        assertThrows(UsernameExistException.class, () -> userService.updateUser("johnDo", user));
    }

    @Test
    void throwsUsernameNotFoundExceptionOnUpdateUser() {
        UserDTO user= UserDTO.builder()
                .email("john@gmail.com")
                .username("valP")
                .build();
        assertThrows(UserNotFoundException.class, () -> userService.updateUser("johny", user));
    }

    @Test
    void throwsEmailNotFoundExceptionOnResetPassword() {
        assertThrows(EmailNotFoundException.class, () -> userService.resetPassword("email@email.com"));
    }

    @Test
    void resetPassword() throws EmailNotFoundException, MessagingException {
        userService.resetPassword("val.piho@gmail.com");

        User user = userRepository.findUserByEmail("val.piho@gmail.com");

        assertNotEquals(user.getPassword(), userDtoAsClient.getPassword());
    }

    @Test
    void deleteUser() {
        userService.deleteUser("valP");
        assertThat(userRepository.findUserByUsername("valP")).isNull();
    }

    @Test
    void findUserByUsername() {
    }

    @Test
    void findUserByEmail() {
    }

    @Test
    void findAllUsers() throws UserNotFoundException, EmailExistException, MessagingException, UsernameExistException {
        userService.register(userDtoAsClient);
        List<User> users = userService.findAllUsers("johnDo");

        assertFalse(users.isEmpty());
        assertEquals("valPiho", users.get(0).getUsername());
    }
}