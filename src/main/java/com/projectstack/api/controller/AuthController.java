
package com.projectstack.api.controller;

import com.projectstack.api.model.TokenBlacklist;
import com.projectstack.api.payload.request.LoginRequest;
import com.projectstack.api.payload.request.SignupRequest;
import com.projectstack.api.payload.response.JwtResponse;
import com.projectstack.api.payload.response.MessageResponse;
import com.projectstack.api.repository.TokenBlacklistRepository;
import com.projectstack.api.repository.UserRepository;
import com.projectstack.api.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;

import com.projectstack.api.payload.request.OTPRequest;
import com.projectstack.api.payload.response.OTPResponse;
import com.projectstack.api.service.OTPService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getEmail());
            JwtResponse response = authService.authenticateUser(loginRequest);
            logger.info("Login successful for user: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.warn("Login failed - invalid credentials: {}", loginRequest.getEmail());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid email or password", false));
        } catch (Exception e) {
            logger.error("Login error: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred during login", false, e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            logger.info("Registration attempt for user: {}", signUpRequest.getEmail());
            
            // Validate email uniqueness
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                logger.warn("Registration failed - email already in use: {}", signUpRequest.getEmail());
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Email is already in use", false));
            }

            JwtResponse response = authService.registerUser(signUpRequest);
            logger.info("Registration successful for user: {}", signUpRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed - validation error: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage(), false));
        } catch (Exception e) {
            logger.error("Registration error: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error during registration", false, e.getMessage()));
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuthStatus() {
        return ResponseEntity.ok(new MessageResponse("Authentication service is running", true));
    }

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
        }

        if (!tokenBlacklistRepository.existsByToken(token)) {
            long expirationTime = System.currentTimeMillis() + 86400000; // 24 hours
            TokenBlacklist blacklistedToken = new TokenBlacklist(token, expirationTime);
            tokenBlacklistRepository.save(blacklistedToken);
        }

        return ResponseEntity.ok(new MessageResponse("User logged out successfully", true));
    }


    @Autowired
    private OTPService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<MessageResponse> sendOtp(@RequestParam String email) {
        String otp = otpService.generateOTP();
        System.out.println("Generated OTP: " + otp);  // Debugging purpose
        otpService.sendOTPEmail(email);
        return ResponseEntity.ok(new MessageResponse("OTP sent successfully", true));
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<OTPResponse> verifyOtp(@RequestBody OTPRequest otpRequest) {
        boolean isValid = otpService.validateOTP(otpRequest.getOtp());
        if (isValid) {
            return ResponseEntity.ok(new OTPResponse("OTP verified successfully", true));
        } else {
            return ResponseEntity.badRequest().body(new OTPResponse("Invalid OTP", false));
        }
    }
}
