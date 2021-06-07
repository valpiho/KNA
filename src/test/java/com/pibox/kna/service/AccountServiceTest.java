package com.pibox.kna.service;

import com.pibox.kna.domain.User;
import com.pibox.kna.exceptions.domain.EmailExistException;
import com.pibox.kna.exceptions.domain.UserExistException;
import com.pibox.kna.exceptions.domain.NotFoundException;
import com.pibox.kna.exceptions.domain.UsernameExistException;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    UserDTO userDtoAsDriver= UserDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@ail.com")
            .username("johnDoe")
            .isClientOrDriver(false)
            .driverPlateNumber("323 BDX")
            .build();

    UserDTO userDtoAsClient= UserDTO.builder()
            .firstName("Val")
            .lastName("Piho")
            .email("val.piho@ail.com")
            .username("valPiho")
            .isClientOrDriver(true)
            .clientEmail("company@ail.com")
            .clientPhoneNumber("4343490332")
            .clientCountry("Italy")
            .clientCity("Roma")
            .clientStreetAddress("Alloha 12")
            .clientZipCode("32321")
            .build();

    @BeforeEach
    void setUp() throws NotFoundException, EmailExistException, MessagingException, UsernameExistException {
        userService.register(userDtoAsDriver);
        userService.register(userDtoAsClient);
    }

    @Test
    void addContact() throws NotFoundException, UserExistException {
        accountService.addContact(userDtoAsClient.getUsername(), userDtoAsDriver.getUsername());
        User user = userRepository.findUserByUsername(userDtoAsClient.getUsername());

        assertThat(user.getContacts().get(0).getUsername()).isEqualTo(userDtoAsDriver.getUsername());

    }

    @Test
    void throwsUsernameNotFoundExceptionOnAddContact() {
        assertThrows(NotFoundException.class, () -> accountService.addContact(userDtoAsClient.getUsername(), "john"));
    }

    @Test
    void throwsUserExistExceptionOnAddContact() throws NotFoundException, UserExistException {
        accountService.addContact(userDtoAsClient.getUsername(), userDtoAsDriver.getUsername());
        assertThrows(UserExistException.class, () -> accountService.addContact(userDtoAsClient.getUsername(), "johnDoe"));
    }

    @Test
    void removeContact() throws NotFoundException, UserExistException {
        accountService.addContact(userDtoAsClient.getUsername(), userDtoAsDriver.getUsername());

        accountService.removeContact(userDtoAsClient.getUsername(), userDtoAsDriver.getUsername());
        User user = userRepository.findUserByUsername(userDtoAsClient.getUsername());

        assertThat(user.getContacts().size()).isEqualTo(0);
    }

    @Test
    void throwsUsernameNotFoundExceptionOnRemoveContact() {
        assertThrows(NotFoundException.class, () -> accountService.removeContact(userDtoAsClient.getUsername(), "john"));
    }

    @Test
    void throwsUsernameNotFoundExceptionNoContainsOnRemoveContact() {
        assertThrows(NotFoundException.class, () -> accountService.removeContact(userDtoAsClient.getUsername(), "johnDoe"));
    }
}