package com.pibox.kna.web.rest;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.HttpResponse;
import com.pibox.kna.domain.User;
import com.pibox.kna.exceptions.domain.*;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.AccountService;
import com.pibox.kna.service.utility.MapperService;
import com.pibox.kna.service.UserService;
import com.pibox.kna.service.dto.UserDTO;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@Validated
@RequestMapping("/api/v1/account")
public class AccountResource {

    private final AccountService accountService;
    private final UserService userService;
    private final MapperService mapper;
    private final JWTTokenProvider jwtTokenProvider;

    public AccountResource(AccountService accountService,
                           UserService userService,
                           MapperService mapper,
                           JWTTokenProvider jwtTokenProvider) {
        this.accountService = accountService;
        this.userService = userService;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        User user = userService.findUserByUsername(authUsername);
        return getResponseEntity(user);
    }

    @PatchMapping
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                        @Valid @RequestBody UserDTO userDTO)
            throws NotFoundException, EmailExistException, UsernameExistException {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        User updatedUser = userService.updateUser(authUsername, userDTO);
        return getResponseEntity(updatedUser);
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<UserMiniDTO>> getUserContacts(@RequestHeader("Authorization") String token) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<User> contacts = userService.findUserByUsername(authUsername).getContacts();
        return new ResponseEntity<>(mapper.toListOfUserMiniDTO(contacts), OK);
    }

    @PatchMapping("/contacts/add")
    public ResponseEntity<HttpResponse> addContact(@RequestHeader("Authorization") String token,
                                                   @RequestParam("username") @Size(
                                                           min = 2, max = 20, message = "Must be between 2 and 20 characters")
                                                           String username)
            throws NotFoundException, UserExistException {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        accountService.addContact(authUsername, username);
        return response(OK, "Contact successfully added");
    }

    @PatchMapping("/contacts/remove")
    public ResponseEntity<HttpResponse> removeContact(@RequestHeader("Authorization") String token,
                                                      @RequestParam("username") @Size(
                                                              min = 2, max = 20, message = "Must be between 2 and 20 characters")
                                                              String username)
            throws NotFoundException {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        accountService.removeContact(authUsername, username);
        return response(OK, "Contact successfully removed");
    }

    private ResponseEntity<?> getResponseEntity(User user) {
        if (user.getRole().equals(Role.ROLE_DRIVER.name())) {
            return new ResponseEntity<>(mapper.toDriverDto(user), OK);
        }
        return new ResponseEntity<>(mapper.toClientDto(user), OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, message), httpStatus);
    }
}
