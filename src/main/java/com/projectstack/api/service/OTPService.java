package com.projectstack.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private EmailService emailService;

    private String generatedOTP;

    public String generateOTP() {
        generatedOTP = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        return generatedOTP;
    }

    public void sendOTPEmail(String email) {
        emailService.sendEmail(email, "Your OTP Code", "Your OTP is: " + generatedOTP + "\nValid for 5 minutes.");
    }

    public boolean validateOTP(String inputOTP) {
        return inputOTP.equals(generatedOTP);
    }
}
