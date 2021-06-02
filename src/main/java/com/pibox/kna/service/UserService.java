package com.pibox.kna.service;

import com.pibox.kna.domain.Client;
import com.pibox.kna.domain.Driver;
import com.pibox.kna.domain.User;
import com.pibox.kna.domain.UserPrincipal;
import com.pibox.kna.exceptions.domain.*;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.UserDTO;
import com.pibox.kna.service.utility.MailService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import java.util.Date;
import java.util.List;

import static com.pibox.kna.constants.FileConstant.*;
import static com.pibox.kna.constants.UserConstant.*;
import static com.pibox.kna.domain.Enumeration.Role.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       MailService mailService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            userRepository.save(user);
            return new UserPrincipal(user);
        }
    }

    public void register(UserDTO userDTO) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, userDTO.getUsername(), userDTO.getEmail());
        User user = new User();
        String password = generatePassword();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodePassword(password));
        user.setImageUrl(getTemporaryProfileImageUrl(userDTO.getUsername()));
        user.setActive(true);
        user.setJoinDate(new Date());
        if (userDTO.isClientOrDriver()) {
            user.setRole(ROLE_CLIENT.name());
            user.setAuthorities(ROLE_CLIENT.getAuthorities());
            user.setClient(new Client(
                    userDTO.getClientEmail(),
                    userDTO.getClientPhoneNumber(),
                    userDTO.getClientCountry(),
                    userDTO.getClientCity(),
                    userDTO.getClientStreetAddress(),
                    userDTO.getClientZipCode()));
        } else {
            user.setRole(ROLE_DRIVER.name());
            user.setAuthorities(ROLE_DRIVER.getAuthorities());
            user.setDriver(new Driver(userDTO.getDriverPlateNumber()));
        }
        userRepository.save(user);
        mailService.sendNewPasswordEmail(userDTO.getFirstName(), password, userDTO.getEmail());
        System.out.println(password);
    }

    public User updateUser(String username, UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException {
        User user = validateNewUsernameAndEmail(username, userDTO.getUsername(), userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getDriverPlateNumber() != null) {
            user.getDriver().setPlateNumber(userDTO.getDriverPlateNumber());
        } else {
            user.getClient().setEmail(userDTO.getClientEmail());
            user.getClient().setPhoneNumber(userDTO.getClientPhoneNumber());
            user.getClient().setCountry(userDTO.getClientCountry());
            user.getClient().setCity(userDTO.getClientCity());
            user.getClient().setStreetAddress(userDTO.getClientStreetAddress());
            user.getClient().setZipCode(userDTO.getClientZipCode());
        }
        userRepository.save(user);
        return user;
    }

    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        mailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    public void deleteUser(String username) {
        User user = userRepository.findUserByUsername(username);
        userRepository.deleteById(user.getId());
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public List<User> findAllUsers(String username) {
        return userRepository.findAllByUsernameNot(username);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if(userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }
}
