package com.hendisantika.usermanagement.service;

import com.hendisantika.usermanagement.entity.Role;
import com.hendisantika.usermanagement.entity.User;
import com.hendisantika.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;


    @Test
    void shouldLoadUserSuccessfully() {
        // GIVEN
        Role role = new Role();
        role.setDescription("ROLE_USER");

        User user = new User();
        user.setUsername("ayb");
        user.setPassword("password");
        user.setRoles(Set.of(role));

        when(userRepository.findByUsername("ayb"))
                .thenReturn(Optional.of(user));

        // WHEN
        UserDetails userDetails =
                userDetailsService.loadUserByUsername("ayb");

        // THEN
        assertNotNull(userDetails);
        assertEquals("ayb", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }


    @Test
    void shouldLoadUserWithNoRoles() {
        // GIVEN
        User user =  new User();
        user.setUsername("noRoleUser");
        user.setPassword("password");
        user.setRoles(Set.of());

        when(userRepository.findByUsername("noRoleUser"))
                .thenReturn(Optional.of(user));

        // WHEN
        UserDetails userDetails =
                userDetailsService.loadUserByUsername("noRoleUser");

        // THEN
        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().isEmpty());
    }


    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // THEN
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("unknown"));
    }
}
