package com.pibox.kna.web.rest;

import com.pibox.kna.domain.HttpResponse;
import com.pibox.kna.domain.User;
import com.pibox.kna.domain.UserPrincipal;
import com.pibox.kna.domain.form.UserRegistrationForm;
import com.pibox.kna.exceptions.domain.EmailExistException;
import com.pibox.kna.exceptions.domain.EmailNotFoundException;
import com.pibox.kna.exceptions.domain.UserNotFoundException;
import com.pibox.kna.exceptions.domain.UsernameExistException;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.UserService;
import com.pibox.kna.service.dto.UserDTO;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import java.util.List;
import java.util.stream.Collectors;

import static com.pibox.kna.constants.EmailConstant.NEW_PASSWORD_EMAIL_SENT;
import static com.pibox.kna.constants.SecurityConstant.JWT_TOKEN_HEADER;
import static com.pibox.kna.constants.UserConstant.USER_REGISTERED;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping({"/api/v1", "/api/v1/account"})
public class AccountResource {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JWTTokenProvider jwtTokenProvider;

    public AccountResource(AuthenticationManager authenticationManager,
                           UserService userService,
                           ModelMapper modelMapper,
                           JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody UserRegistrationForm regForm)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        userService.register(regForm);
        return response(CREATED, USER_REGISTERED);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, NEW_PASSWORD_EMAIL_SENT + email);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(modelMapper.map(loginUser, UserDTO.class), jwtHeader, OK);
    }

    @GetMapping("/account")
    public ResponseEntity<UserDTO> getUser(@RequestHeader("Authorization") String token) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        User user = userService.findUserByUsername(authUsername);
        return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), OK);
    }

    @PatchMapping("/account")
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization") String token,
                                              @RequestBody UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        User updatedUser = userService.updateUser(authUsername, userDTO);
        return new ResponseEntity<>(modelMapper.map(updatedUser, UserDTO.class), OK);
    }

    @GetMapping("/account/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable(value = "username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), OK);
    }

    @GetMapping("/account/contacts")
    // TODO: Refactor code
    public ResponseEntity<List<UserMiniDTO>> getUserContacts(@RequestHeader("Authorization") String token) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<User> contacts = userService.findUserByUsername(authUsername).getContacts();

        Converter<?, Boolean> check = ctx -> ctx.getSource() != null;
        modelMapper.typeMap(User.class, UserMiniDTO.class)
                .addMappings(mapper -> mapper.using(check).map(User::getDriver, UserMiniDTO::setDriver))
                .addMappings(mapper -> mapper.using(check).map(User::getClient, UserMiniDTO::setClient));
        List<UserMiniDTO> contactsDto = contacts.stream()
                .map(user -> modelMapper.map(user, UserMiniDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(contactsDto, OK);
    }

    @PatchMapping("/account/contacts/add")
    // TODO: Check if user is already in contacts list
    public ResponseEntity<HttpResponse> addContact(@RequestHeader("Authorization") String token,
                                                   @RequestParam("username") String username) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        userService.addContact(authUsername, username);
        return response(OK, "Contact successfully added");
    }

    @PatchMapping("/account/contacts/remove")
    // TODO: Check if user is in contacts list
    public ResponseEntity<HttpResponse> removeContact(@RequestHeader("Authorization") String token,
                                                      @RequestParam("username") String username) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        userService.removeContact(authUsername, username);
        return response(OK, "Contact successfully removed");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, message), httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
