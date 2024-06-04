package org.example.userserviceandmanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.userserviceandmanagement.dtos.SendEmailEventDto;
import org.example.userserviceandmanagement.models.Token;
import org.example.userserviceandmanagement.models.User;
import org.example.userserviceandmanagement.repositories.TokenRepository;
import org.example.userserviceandmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;


@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       TokenRepository tokenRepository,
                       KafkaTemplate<String, String> kafkaTemplate,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository= tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    public User signUp(String name, String email, String password) {
        User newUser = new User();
        newUser.setUserName(name);
        newUser.setEmail(email);
        newUser.setHashPassword(bCryptPasswordEncoder.encode(password));

        SendEmailEventDto sendEmailEventDto = new SendEmailEventDto();
        sendEmailEventDto.setTo(email);
        sendEmailEventDto.setSubject("Welcome to DECommerceApp");
        sendEmailEventDto.setBody("Thanks! for signing up at our platform." +
                "We are looking forward to  serve you our best.");
        sendEmailEventDto.setFrom(email);

        //Json representation of above dto


        try {
            kafkaTemplate.send(
                    "sendEmail",
                    objectMapper.writeValueAsString(sendEmailEventDto)

            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return userRepository.save(newUser);
    }

    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            return null;// throe User not existing exception
        }
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password, user.getHashPassword())){
            // throw invalid password exception
            return null;
        }

        Token token = new Token();
        token.setUser(user);
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plus(30, ChronoUnit.DAYS);


        // Convert LocalDate to Date
        Date expiryDate = Date.from(thirtyDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpirationDate(expiryDate);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        return tokenRepository.save(token);
    }


    public void logout(String tokenValue) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedStatus(tokenValue, false);
        if(optionalToken.isEmpty()){
            // throw token not found exception or already expired exception
            return;
        }
        Token token = optionalToken.get();
        token.setDeletedStatus(true);
        tokenRepository.save(token);
    }

    public User validateToken(String token){
        Optional<Token> optionalToken =tokenRepository.findByValueAndDeletedStatusAndExpirationDateGreaterThan(token, false, new Date());
        if(optionalToken.isEmpty()){
            return null;
        }
        return optionalToken.get().getUser();
    }
}
