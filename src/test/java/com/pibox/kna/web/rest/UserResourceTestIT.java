package com.pibox.kna.web.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

class UserResourceTestIT extends AbstractTestIT{

    @Test
    public void shouldNotAllowAccessToUnauthenticatedUsers() throws Exception {
        mockMvc.perform(get("/api/v1/testURL"))
                .andExpect(status().isForbidden());
    }

    @Test
    void registerUserAsDriver() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDtoAsDriver)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User has been registered successfully")));
    }

    @Test
    void registerUserAsClient() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDtoAsClient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User has been registered successfully")));
    }

    @Test
    void userCanResetPassword() throws Exception {
        mockMvc.perform(get("/api/v1/reset-password/val.piho@ail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("An email with a new password was sent to: val.piho@ail.com")));
    }

    @Test
    public void userCanToUsersList() throws Exception {
        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/users")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }




    @Test
    void getAllUsers() {
    }

    @Test
    void getUserByUsername() {
    }

    @Test
    void addNewUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getTempProfileImage() {
    }
}