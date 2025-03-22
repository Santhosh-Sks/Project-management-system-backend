package com.projectstack.api.repository;

import com.projectstack.api.model.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OTPRepository extends MongoRepository<OTP, String> {
    Optional<OTP> findByEmail(String email);
}
