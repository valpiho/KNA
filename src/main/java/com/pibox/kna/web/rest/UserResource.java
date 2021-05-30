package com.pibox.kna.web.rest;

import com.pibox.kna.domain.User;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.UserService;
import com.pibox.kna.service.dto.UserMiniDTO;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserResource {

    private final UserService userService;
    private final ModelMapper  modelMapper;
    private final JWTTokenProvider jwtTokenProvider;

    public UserResource(UserService userService,
                        ModelMapper modelMapper,
                        JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    // TODO: Refactor code
    public ResponseEntity<List<UserMiniDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<User> users = userService.findAllUsers(username);

        Converter<?, Boolean> check = ctx -> ctx.getSource() != null;
        modelMapper.typeMap(User.class, UserMiniDTO.class)
                .addMappings(mapper -> mapper.using(check).map(User::getDriver, UserMiniDTO::setDriver))
                .addMappings(mapper -> mapper.using(check).map(User::getClient, UserMiniDTO::setClient));
        List<UserMiniDTO> usersMiniDto = users.stream()
                .map(user -> modelMapper.map(user, UserMiniDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(usersMiniDto, OK);
    }
}
