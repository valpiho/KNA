package com.pibox.kna.web.rest;

import com.pibox.kna.domain.User;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.ModelMapperService;
import com.pibox.kna.service.UserService;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserResource {

    private final UserService userService;
    private final ModelMapperService mapper;
    private final JWTTokenProvider jwtTokenProvider;

    public UserResource(UserService userService,
                        ModelMapper modelMapper,
                        ModelMapperService mapper,
                        JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    // TODO: Refactor code
    public ResponseEntity<List<UserMiniDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<User> users = userService.findAllUsers(username);
        return new ResponseEntity<>(mapper.convertToListOfUserMiniDTO(users), OK);
    }
}
