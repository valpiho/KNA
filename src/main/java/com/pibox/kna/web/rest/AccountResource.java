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
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        UserDTO userDto = modelMapper.map(loginUser, UserDTO.class);
        return new ResponseEntity<>(userDto, jwtHeader, OK);
    }

    @GetMapping("/account/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable(value = "username") String username) {
        UserDTO user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/account")
    public ResponseEntity<UserDTO> getUser(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromDecodedToken(token);
        UserDTO user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @PatchMapping("/account")
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization") String token,
                                              @RequestBody UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException {
        String username = jwtTokenProvider.getUsernameFromDecodedToken(token);
        User updatedUser = userService.updateUser(username, userDTO);
        return new ResponseEntity<>(modelMapper.map(updatedUser, UserDTO.class), OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, NEW_PASSWORD_EMAIL_SENT + email);
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
