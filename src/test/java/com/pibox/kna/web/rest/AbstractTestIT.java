package com.pibox.kna.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pibox.kna.domain.Client;
import com.pibox.kna.domain.Driver;
import com.pibox.kna.domain.User;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.LoginDTO;
import com.pibox.kna.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static com.pibox.kna.domain.Enumeration.Role.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-it.properties")
@AutoConfigureMockMvc
@Transactional
public class AbstractTestIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    protected UserRepository userRepository;

    UserDTO userDtoAsDriver= UserDTO.builder()
            .firstName("John").lastName("Doe")
            .email("johndoe@ail.com").username("johnDoe")
            .isClientOrDriver(false)
            .driverPlateNumber("323 BDX")
            .build();

    UserDTO userDtoAsClient= UserDTO.builder()
            .firstName("Susan").lastName("Doe")
            .email("susan.doe@ail.com").username("susanDoe")
            .isClientOrDriver(true)
            .clientEmail("company@ail.com").clientPhoneNumber("4343490332")
            .clientCountry("Norway").clientCity("Oslo").clientStreetAddress("Sunset 13").clientZipCode("32321")
            .build();

    User user1;
    User user2;
    User admin;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setFirstName("Val");
        user1.setLastName("Piho");
        user1.setUsername("valPiho");
        user1.setEmail("val.piho@ail.com");
        user1.setPassword(bCryptPasswordEncoder.encode("pq1Ax0k1BW"));
        user1.setActive(true);
        user1.setImageUrl("http://localhost:8080/api/v1/image/valPiho");
        user1.setJoinDate(new Date());
        user1.setRole(ROLE_DRIVER.name());
        user1.setAuthorities(ROLE_DRIVER.getAuthorities());
        user1.setDriver(new Driver("3232"));

        user2 = new User();
        user1.setFirstName("Alex");
        user1.setLastName("Piho");
        user2.setUsername("alexPiho");
        user2.setEmail("alex.piho@ail.com");
        user2.setPassword(bCryptPasswordEncoder.encode("Y65QXnY6fP"));
        user2.setActive(true);
        user2.setImageUrl("http://localhost:8080/api/v1/image/alexPiho");
        user2.setJoinDate(new Date());
        user2.setRole(ROLE_CLIENT.name());
        user2.setAuthorities(ROLE_CLIENT.getAuthorities());
        user2.setClient(new Client("company@ail.com", "34234234234", "Estonia", "Tallinn", "Liivalaia 13", "42342"));

        admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@ail.com");
        admin.setPassword(bCryptPasswordEncoder.encode("GcyeBV6xPx"));
        admin.setActive(true);
        admin.setRole(ROLE_ADMIN.name());
        admin.setAuthorities(ROLE_ADMIN.getAuthorities());

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(admin);
    }

    protected ResultActions login(String username, String password) throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setUsername(username);
        loginDto.setPassword(password);
        return mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginDto)));
    }

    protected String extractToken(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getHeader("Jwt-Token");
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
