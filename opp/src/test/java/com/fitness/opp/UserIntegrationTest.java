package com.fitness.opp;

import com.fitness.opp.models.User;
import com.fitness.opp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void testScenariu1_CreateUser() throws Exception {
        // Am schimbat URL-ul din /users/save-view in /users/save conform Controllerului tau
        mockMvc.perform(post("/users/save")
                        .with(csrf())
                        .param("username", "integrare_test")
                        .param("password", "pass123")
                        .param("email", "test@integrare.com")
                        .param("role", "ROLE_USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/list"));

        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void testScenariu2_UpdateUser() throws Exception {
        User user = new User();
        user.setUsername("vechi");
        user.setPassword("123");
        user.setEmail("vechi@gmail.com");
        user.setRole("ROLE_USER");
        user = userRepository.save(user);

        // Am schimbat URL-ul in /users/save
        mockMvc.perform(post("/users/save")
                        .with(csrf())
                        .param("id", user.getId().toString())
                        .param("username", "nou_update")
                        .param("email", "nou@gmail.com")
                        .param("role", "ROLE_USER"))
                .andExpect(status().is3xxRedirection());

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("nou_update", updated.getUsername());
    }

    @Test
    void testScenariu3_DeleteUser() throws Exception {
        User user = new User();
        user.setUsername("de_sters");
        user.setPassword("123");
        user.setEmail("sters@gmail.com");
        user.setRole("ROLE_USER");
        user = userRepository.save(user);

        // In Controller ai @GetMapping("/delete") cu @RequestParam("userId")
        // Deci URL-ul corect este /users/delete?userId=ID
        mockMvc.perform(get("/users/delete")
                        .param("userId", user.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/list"));

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}