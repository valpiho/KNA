package com.pibox.kna.web.rest;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.User;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.utility.MapperService;
import com.pibox.kna.service.UserService;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserResource {

    private final UserService userService;
    private final MapperService mapper;
    private final JWTTokenProvider jwtTokenProvider;

    public UserResource(UserService userService,
                        MapperService mapper,
                        JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<List<UserMiniDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<User> users = userService.findAllUsers(username);
        return new ResponseEntity<>(mapper.convertToListOfUserMiniDTO(users), OK);
    }

    @GetMapping("/account/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username") String username) {
        User user = userService.findUserByUsername(username);
        return getResponseEntity(user);
    }

    private ResponseEntity<?> getResponseEntity(User user) {
        if (user.getRole().equals(Role.ROLE_DRIVER.name())) {
            return new ResponseEntity<>(mapper.toDriverDto(user), OK);
        }
        return new ResponseEntity<>(mapper.toClientDto(user), OK);
    }
}
