package com.fitness.opp.controller;

import com.fitness.opp.models.User;
import com.fitness.opp.models.UserProfile;
import com.fitness.opp.repository.UserProfileRepository;
import com.fitness.opp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileRepository profileRepository;

    @MockBean
    private UserRepository userRepository;

    
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testListProfiles() throws Exception {
        Page<UserProfile> emptyPage = new PageImpl<>(Collections.emptyList());
        when(profileRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/profile/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("profiles-list"))
                .andExpect(model().attributeExists("profiles"));
    }

    
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testShowFormForAdd() throws Exception {
        mockMvc.perform(get("/profile/showFormForAdd"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-form"))
                .andExpect(model().attributeExists("userProfile"))
                .andExpect(model().attributeExists("users"));
    }

    
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testShowFormForUpdate() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/profile/showFormForUpdate").param("profileId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-form"))
                .andExpect(model().attribute("isUpdate", true));
    }

    
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testSaveProfileSuccess() throws Exception {
        UserProfile profile = new UserProfile();
        User user = new User();
        user.setId(1L);
        profile.setUser(user);

        mockMvc.perform(post("/profile/save")
                        .with(csrf()) // Previne 403 Forbidden
                        .flashAttr("userProfile", profile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/list"));

        verify(profileRepository, times(1)).save(any(UserProfile.class));
    }

    
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testSaveProfileError() throws Exception {
        mockMvc.perform(post("/profile/save")
                        .with(csrf()) // Previne 403 Forbidden
                        .flashAttr("userProfile", new UserProfile()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/showFormForAdd?error=missingUser"));
    }

    
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testDeleteProfile() throws Exception {
        Long profileId = 1L;
        UserProfile profile = new UserProfile();
        User user = new User();
        profile.setUser(user);

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/profile/delete").param("profileId", profileId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/list"));

        
        verify(userRepository, times(1)).save(any(User.class));
        verify(profileRepository, times(1)).delete(any(UserProfile.class));
    }
}