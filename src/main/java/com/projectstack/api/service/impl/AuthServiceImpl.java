
package com.projectstack.api.service.impl;

import com.projectstack.api.model.User;
import com.projectstack.api.payload.request.LoginRequest;
import com.projectstack.api.payload.request.SignupRequest;
import com.projectstack.api.payload.response.JwtResponse;
import com.projectstack.api.repository.UserRepository;
import com.projectstack.api.security.JwtTokenProvider;
import com.projectstack.api.security.UserDetailsImpl;
import com.projectstack.api.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            logger.error("Invalid login request: missing required fields");
            throw new IllegalArgumentException("Email and password are required");
        }
        
        logger.info("Authentication attempt for user: {}", loginRequest.getEmail());
        
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);
            logger.info("JWT token generated successfully for user: {}", loginRequest.getEmail());

            // Get user details from authentication
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Return the JWT response
            return new JwtResponse(
                    userDetails.getId(),
                    userDetails.getName(),
                    userDetails.getUsername(),
                    jwt);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            throw e;
        }
    }

    @Override
    public JwtResponse registerUser(SignupRequest signUpRequest) {
        // Validate the request
        if (signUpRequest == null || 
            signUpRequest.getEmail() == null || 
            signUpRequest.getPassword() == null ||
            signUpRequest.getName() == null) {
            logger.error("Invalid signup request: missing required fields");
            throw new IllegalArgumentException("Name, email, and password are required");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Registration failed: Email already in use - {}", signUpRequest.getEmail());
            throw new IllegalArgumentException("Email is already in use!");
        }
        
        try {
            // Create new user account
            User user = new User();
            user.setName(signUpRequest.getName());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(encoder.encode(signUpRequest.getPassword()));
            user.setPhone(signUpRequest.getPhone());
            
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_USER");
            user.setRoles(roles);

            // Save user to database
            User savedUser = userRepository.save(user);
            
            logger.info("User registered successfully: {}", savedUser.getEmail());

            // For automatic login after registration, generate token
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            return new JwtResponse(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    jwt);
        } catch (Exception e) {
            logger.error("Error during registration: ", e);
            throw e;
        }
    }
    
    // Helper method to check if a user exists by ID
    public boolean userExists(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent();
    }
}
