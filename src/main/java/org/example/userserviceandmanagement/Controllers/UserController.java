package org.example.userserviceandmanagement.Controllers;

import lombok.NonNull;
import org.example.userserviceandmanagement.dtos.LoginRequestDto;
import org.example.userserviceandmanagement.dtos.LogoutRequestDto;
import org.example.userserviceandmanagement.dtos.SignUpRequestDto;
import org.example.userserviceandmanagement.dtos.UserDto;
import org.example.userserviceandmanagement.models.Token;
import org.example.userserviceandmanagement.models.User;
import org.example.userserviceandmanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;


    // In controller @Autowired is optional
    public UserController(UserService userService) {

        this.userService = userService;

    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto requestDto){
        return userService.login(requestDto.getEmail(), requestDto.getPassword());
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody SignUpRequestDto requestDto){
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String name = requestDto.getName();
        return userService.signUp(name, email, password);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto requestDto){
        // delete token if it exists and return 200
        // else return 404

        // Delete taken if it exists and return 200
        // Soft Delete -> We do not delete the token rather we change the status as True to isDeleted
        userService.logout(requestDto.getToken());
        return new ResponseEntity<>(HttpStatus.OK);


    }


    @PostMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable("token") @NonNull String token){
        return UserDto.from(userService.validateToken(token));
    }
}
