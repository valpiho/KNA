package com.pibox.kna.service;

import com.pibox.kna.domain.User;
import com.pibox.kna.exceptions.domain.UserExistException;
import com.pibox.kna.exceptions.domain.NotFoundException;
import com.pibox.kna.repository.UserRepository;
import org.springframework.stereotype.Service;

import static com.pibox.kna.constants.UserConstant.NO_USER_FOUND_BY_USERNAME;
import static com.pibox.kna.constants.UserConstant.USER_EXIST;

@Service
public class AccountService {

    private final UserRepository userRepository;

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addContact(String authUsername, String username) throws NotFoundException, UserExistException {
        User user = userRepository.findUserByUsername(authUsername);
        User addUser = userRepository.findUserByUsername(username);
        if(addUser == null) {
            throw new NotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }
        if (user.getContacts().contains(addUser)) {
            throw new UserExistException(USER_EXIST);
        }
        user.getContacts().add(addUser);
    }

    public void removeContact(String authUsername, String username) throws NotFoundException {
        User user = userRepository.findUserByUsername(authUsername);
        User removeUser = userRepository.findUserByUsername(username);
        if(removeUser == null) {
            throw new NotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }
        if (!user.getContacts().contains(removeUser)) {
            throw new NotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }
        user.getContacts().remove(removeUser);
    }
}
