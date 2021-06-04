package com.pibox.kna.web.rest;

import com.pibox.kna.service.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

class UserResourceTestIT extends AbstractTestIT{

    @Test
    public void shouldNotAllowAccessToUnauthenticatedUsers() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/testURL"));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void getUserAvatar() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/image/alexPiho"));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void registerUserAsDriver() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDtoAsDriver)));

        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User has been registered successfully")));
    }

    @Test
    void registerUserAsClient() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDtoAsClient)));

        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User has been registered successfully")));
    }

    @Test
    void userCanResetPassword() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/reset-password/val.piho@ail.com"));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("An email with a new password was sent to: val.piho@ail.com")));
    }

    @Test
    public void userCanAccessToUsersList() throws Exception {
        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/users")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userCanAccessToAnotherUserAsDriverAccount() throws Exception {
        final String token = extractToken(login(admin.getUsername(), "GcyeBV6xPx").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/users/valPiho")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userCanAccessToAnotherUserAsClientAccount() throws Exception {
        final String token = extractToken(login(admin.getUsername(), "GcyeBV6xPx").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/users/alexPiho")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userAsAdminCanAddNewUser() throws Exception {
        final String token = extractToken(login(admin.getUsername(), "GcyeBV6xPx").andReturn());
        ResultActions resultActions = mockMvc.perform(post("/api/v1/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDtoAsClient)));

        resultActions.andExpect(status().isCreated());
    }

    @Test
    public void userAsAdminCanUpdateUserAccount() throws Exception {
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
        final String token = extractToken(login(admin.getUsername(), "GcyeBV6xPx").andReturn());
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/users/alexPiho")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userAsAdminCanDeleteUser() throws Exception {
        final String token = extractToken(login(admin.getUsername(), "GcyeBV6xPx").andReturn());
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/users/alexPiho")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }
}