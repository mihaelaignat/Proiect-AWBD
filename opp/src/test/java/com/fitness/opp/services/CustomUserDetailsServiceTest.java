package com.fitness.opp.services;

import com.fitness.opp.models.User;
import com.fitness.opp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private com.fitness.opp.services.CustomUserDetailsService customUserDetailsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUsername("george31");
        sampleUser.setPassword("hashed_password");
        sampleUser.setRole("ADMIN");
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        String searchUsername = "george31";
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(searchUsername);

        assertNotNull(userDetails);
        assertEquals("george31", userDetails.getUsername());
        assertEquals("hashed_password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
    }

    @Test
    void testLoadUserByUsername_ThrowsUsernameNotFoundException() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        String unknownUsername = "user_inexistent";

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(unknownUsername);
        });
    }
}