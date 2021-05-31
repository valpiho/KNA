package com.pibox.kna.service;

import com.pibox.kna.domain.Client;
import com.pibox.kna.domain.Driver;
import com.pibox.kna.domain.User;
import com.pibox.kna.domain.UserPrincipal;
import com.pibox.kna.domain.form.UserRegistrationForm;
import com.pibox.kna.exceptions.domain.*;
import com.pibox.kna.repository.RoleRepository;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.UserDTO;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       MailService mailService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    public void register(UserRegistrationForm regForm) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, regForm.getUsername(), regForm.getPrivateEmail());
        User user = new User();
        String password = generatePassword();
        user.setFirstName(regForm.getFirstName());
        user.setLastName(regForm.getLastName());
        user.setUsername(regForm.getUsername());
        user.setEmail(regForm.getPrivateEmail());
        user.setPassword(encodePassword(password));
        user.setImageUrl(getTemporaryProfileImageUrl(regForm.getUsername()));
        user.setActive(true);
        user.setJoinDate(new Date());
        if (regForm.isClientOrDriver()) {
            user.addRole(roleRepository.findRoleByName("client"));
            user.setClient(new Client(
                    regForm.getCompanyEmail(),
                    regForm.getPhoneNumber(),
                    regForm.getCountry(),
                    regForm.getCity(),
                    regForm.getStreetAddress(),
                    regForm.getZipCode()));
        } else {
            user.addRole(roleRepository.findRoleByName("driver"));
            user.setDriver(new Driver(regForm.getPlateNumber()));
        }
        userRepository.save(user);
        mailService.sendNewPasswordEmail(regForm.getFirstName(), password, regForm.getPrivateEmail());
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

    public void addContact(String authUsername, String username) throws UserNotFoundException, UserExistException {
        User user = findUserByUsername(authUsername);
        User addUser = findUserByUsername(username);
        if(addUser == null) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }
        if (user.getContacts().contains(addUser)) {
            throw new UserExistException(USER_EXIST);
        }
        user.getContacts().add(addUser);
    }

    public void removeContact(String authUsername, String username) throws UserNotFoundException {
        User user = findUserByUsername(authUsername);
        User removeUser = findUserByUsername(username);
        if(removeUser == null) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }
        if (!user.getContacts().contains(removeUser)) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }
        user.getContacts().remove(removeUser);
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
