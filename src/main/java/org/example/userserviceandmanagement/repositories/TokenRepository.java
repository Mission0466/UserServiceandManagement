package org.example.userserviceandmanagement.repositories;

import org.example.userserviceandmanagement.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long>{

    Token save(Token token);

    Optional<Token> findByValueAndDeletedStatus(String value, boolean deletedStatus);

    Optional<Token> findByValueAndDeletedStatusAndExpirationDateGreaterThan(String token,
                                                                            boolean deletedStatus,
                                                                            Date expirationDate);
    //Expiry date should be Greater than current date
}

