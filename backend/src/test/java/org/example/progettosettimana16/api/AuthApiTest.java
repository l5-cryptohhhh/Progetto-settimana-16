package org.example.progettosettimana16.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest extends IntegrationTestSupport {

    private static String registerBody(String username, String email, String password) {
        return "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}".formatted(username, email, password);
    }

    private static String loginBody(String email, String password) {
        return "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password);
    }

    @Test
    void registerReturnsTokenAndUserWithoutPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("giulia.rossi", "giulia@example.com", PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.id").isNumber())
                .andExpect(jsonPath("$.user.username").value("giulia.rossi"))
                .andExpect(jsonPath("$.user.email").value("giulia@example.com"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void registerRejectsDuplicateUsername() throws Exception {
        TestUser existing = registerUser();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody(existing.username(), "diversa@example.com", PASSWORD)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registerRejectsDuplicateEmail() throws Exception {
        TestUser existing = registerUser();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("nome.diverso", existing.email(), PASSWORD)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerValidatesEveryField() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("a", "non-una-email", "corta")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasSize(3)));
    }

    @Test
    void loginWithValidCredentialsReturnsWorkingToken() throws Exception {
        TestUser user = registerUser();

        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(user.email(), PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value(user.username()))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(json, "$.token");

        mockMvc.perform(get("/api/users/me").header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id()))
                .andExpect(jsonPath("$.username").value(user.username()));
    }

    @Test
    void loginWithWrongPasswordIsUnauthorized() throws Exception {
        TestUser user = registerUser();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(user.email(), "password-sbagliata")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void loginWithUnknownEmailIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("nessuno@example.com", PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithoutTokenReturnsJson401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void protectedEndpointWithInvalidTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me").header(AUTHORIZATION, "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }
}
