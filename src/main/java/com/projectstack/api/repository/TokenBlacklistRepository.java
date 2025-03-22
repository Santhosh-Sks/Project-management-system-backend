package com.projectstack.api.repository;

import com.projectstack.api.model.TokenBlacklist;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenBlacklistRepository extends MongoRepository<TokenBlacklist, String> {
    boolean existsByToken(String token);
}
