
package com.projectstack.api.service;

import com.projectstack.api.payload.request.LoginRequest;
import com.projectstack.api.payload.request.SignupRequest;
import com.projectstack.api.payload.response.JwtResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse registerUser(SignupRequest signupRequest);
}
