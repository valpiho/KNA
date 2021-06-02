package com.pibox.kna.service;

import com.pibox.kna.domain.User;
import com.pibox.kna.exceptions.domain.UserExistException;
import com.pibox.kna.exceptions.domain.UserNotFoundException;
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
}
