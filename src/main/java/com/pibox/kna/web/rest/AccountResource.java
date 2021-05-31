package com.pibox.kna.web.rest;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.HttpResponse;
import com.pibox.kna.domain.User;
import com.pibox.kna.domain.UserPrincipal;
import com.pibox.kna.exceptions.domain.*;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.utility.MapperService;
import com.pibox.kna.service.UserService;
import com.pibox.kna.service.dto.UserDTO;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static com.pibox.kna.constants.EmailConstant.NEW_PASSWORD_EMAIL_SENT;
import static com.pibox.kna.constants.FileConstant.*;
import static com.pibox.kna.constants.SecurityConstant.JWT_TOKEN_HEADER;
import static com.pibox.kna.constants.UserConstant.USER_REGISTERED;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping({"/api/v1", "/api/v1/account"})
public class AccountResource {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final MapperService mapper;
    private final JWTTokenProvider jwtTokenProvider;

    public AccountResource(AuthenticationManager authenticationManager,
                           UserService userService,
                           MapperService mapper,
                           JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        userService.register(userDTO);
        return response(CREATED, USER_REGISTERED);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, NEW_PASSWORD_EMAIL_SENT + email);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDto) {
        authenticate(userDto.getUsername(), userDto.getPassword());
        User loginUser = userService.findUserByUsername(userDto.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        if (loginUser.getRole().equals(Role.ROLE_DRIVER.name())) {
            return new ResponseEntity<>(mapper.toDriverDto(loginUser), jwtHeader, OK);
        }
        return new ResponseEntity<>(mapper.toClientDto(loginUser), jwtHeader, OK);
    }

    @GetMapping("/account")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        User user = userService.findUserByUsername(authUsername);
        return getResponseEntity(user);
    }

    @PatchMapping("/account")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                              @RequestBody UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        User updatedUser = userService.updateUser(authUsername, userDTO);
        return getResponseEntity(updatedUser);
    }

    @GetMapping("/account/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username") String username) {
        User user = userService.findUserByUsername(username);
        return getResponseEntity(user);
    }

    @GetMapping("/account/contacts")
    public ResponseEntity<List<UserMiniDTO>> getUserContacts(@RequestHeader("Authorization") String token) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<User> contacts = userService.findUserByUsername(authUsername).getContacts();
        return new ResponseEntity<>(mapper.convertToListOfUserMiniDTO(contacts), OK);
    }

    @PatchMapping("/account/contacts/add")
    public ResponseEntity<HttpResponse> addContact(@RequestHeader("Authorization") String token,
                                                   @RequestParam("username") String username)
            throws UserNotFoundException, UserExistException {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        userService.addContact(authUsername, username);
        return response(OK, "Contact successfully added");
    }

    @PatchMapping("/account/contacts/remove")
    public ResponseEntity<HttpResponse> removeContact(@RequestHeader("Authorization") String token,
                                                      @RequestParam("username") String username)
            throws UserNotFoundException {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        userService.removeContact(authUsername, username);
        return response(OK, "Contact successfully removed");
    }

    @GetMapping(path = "/image/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
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

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
