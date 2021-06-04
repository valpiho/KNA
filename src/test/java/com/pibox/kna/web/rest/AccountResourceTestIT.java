package com.pibox.kna.web.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountResourceTestIT extends AbstractTestIT{

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void userCanAccessAsDriver() throws Exception {
        final String token = extractToken(login(user1.getUsername(), "pq1Ax0k1BW").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/account")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void userCanAccessAsClient() throws Exception {
        final String token = extractToken(login(user2.getUsername(), "Y65QXnY6fP").andReturn());
        ResultActions resultActions = mockMvc.perform(get("/api/v1/account")
                .header("Authorization", "Bearer " + token));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void getUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUserContacts() {
    }

    @Test
    void addContact() {
    }

    @Test
    void removeContact() {
    }
}