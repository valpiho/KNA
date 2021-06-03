package com.pibox.kna.web.rest;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.HttpResponse;
import com.pibox.kna.domain.User;
import com.pibox.kna.domain.UserPrincipal;
import com.pibox.kna.exceptions.domain.EmailExistException;
import com.pibox.kna.exceptions.domain.EmailNotFoundException;
import com.pibox.kna.exceptions.domain.UserNotFoundException;
import com.pibox.kna.exceptions.domain.UsernameExistException;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.dto.LoginDTO;
import com.pibox.kna.service.dto.UserDTO;
import com.pibox.kna.service.utility.MapperService;
import com.pibox.kna.service.UserService;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static com.pibox.kna.constants.EmailConstant.NEW_PASSWORD_EMAIL_SENT;
import static com.pibox.kna.constants.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static com.pibox.kna.constants.SecurityConstant.JWT_TOKEN_HEADER;
import static com.pibox.kna.constants.UserConstant.USER_DELETED_SUCCESSFULLY;
import static com.pibox.kna.constants.UserConstant.USER_REGISTERED;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping({"/api/v1", "/api/v1/users"})
public class UserResource {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final MapperService mapper;
    private final JWTTokenProvider jwtTokenProvider;

    public UserResource(AuthenticationManager authenticationManager,
                        UserService userService,
                        MapperService mapper,
                        JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@Valid @RequestBody UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        userService.register(userDTO);
        return response(CREATED, USER_REGISTERED);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") @Email String email)
            throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, NEW_PASSWORD_EMAIL_SENT + email);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        authenticate(loginDTO.getUsername(), loginDTO.getPassword());
        User loginUser = userService.findUserByUsername(loginDTO.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        if (loginUser.getRole().equals(Role.ROLE_DRIVER.name())) {
            return new ResponseEntity<>(mapper.toDriverDto(loginUser), jwtHeader, OK);
        }
        return new ResponseEntity<>(mapper.toClientDto(loginUser), jwtHeader, OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserMiniDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<User> users = userService.findAllUsers(username);
        return new ResponseEntity<>(mapper.convertToListOfUserMiniDTO(users), OK);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username") String username) {
        User user = userService.findUserByUsername(username);
        return getResponseEntity(user);
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyAuthority('admin:create')")
    public ResponseEntity<HttpResponse> addNewUser(@Valid @RequestBody UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        userService.register(userDTO);
        return response(CREATED, USER_REGISTERED);
    }

    @PatchMapping("/users/{username}")
    @PreAuthorize("hasAnyAuthority('admin:update')")
    public ResponseEntity<?> updateUser(@PathVariable(value = "username") String username,
                                        @Valid @RequestBody UserDTO userDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException {
        User updatedUser = userService.updateUser(username, userDTO);
        return getResponseEntity(updatedUser);
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasAnyAuthority('admin:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
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
