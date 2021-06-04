package com.pibox.kna.web.rest;

import com.pibox.kna.domain.User;
import com.pibox.kna.service.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountResourceTestIT extends AbstractTestIT{

    @Test
    public void userCanAccessAsDriverToUserAccount() throws Exception {
        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/account")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userCanAccessAsClientToUserAccount() throws Exception {
        final String token = extractToken(login(user2.getUsername(), "Y65QXnY6fP").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/account")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userAsDriverCanUpdateUserAccount() throws Exception {
        UserDTO user = UserDTO.builder()
                .firstName("Val")
                .lastName("Piho")
                .username("valPiho")
                .email("val@ail.com")
                .driverPlateNumber("123 ABC")
                .build();
        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/account")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userAsClientCanUpdateUserAccount() throws Exception {
        UserDTO user = UserDTO.builder()
                .firstName("Alex")
                .lastName("Piho")
                .username("alexPiho")
                .email("alex@ail.com")
                .clientEmail("companyX@ail.com")
                .clientPhoneNumber("423423")
                .clientCountry("Latvia")
                .clientCity("Riga")
                .clientStreetAddress("Lotusa 13")
                .clientZipCode("12333")
                .build();
        final String token = extractToken(login(user2.getUsername(), "Y65QXnY6fP").andReturn());
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/account")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userCanAccessToUserContacts() throws Exception {
        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/account/contacts")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userCanAddAnotherUserToUserContacts() throws Exception {
        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/account/contacts/add?username=alexPiho")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Contact successfully added")));
    }

    @Test
    public void userCanRemoveAnotherUserFromUserContacts() throws Exception {
        User user = userRepository.findUserByUsername(user1.getUsername());
        user.getContacts().add(user2);
        userRepository.save(user);

        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/account/contacts/remove?username=alexPiho")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Contact successfully removed")));
    }
}